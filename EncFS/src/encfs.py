"""A naive passthrough FUSE filesystem.

Note, the documentation present here is documentation for the C FUSE API, and
may not correspond 1-to-1 with the Python FUSE API. It is included for
informational purposes only to highlight the intended purpose of each method.

"""

from __future__ import with_statement

from functools import wraps
from getpass import getpass
from queue import PriorityQueue
import os
import sys
import errno
import logging

from fuse import FUSE, FuseOSError, Operations
import inspect


import base64
from cryptography.fernet import Fernet
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC

log = logging.getLogger(__name__)


def logged(f):
    @wraps(f)
    def wrapped(*args, **kwargs):
        log.info('%s(%s)', f.__name__, ','.join([str(item) for item in args[1:]]))
        return f(*args, **kwargs)
    return wrapped



class EncFS(Operations):

    # Static variables here

    """A simple passthrough interface.

    Initialize the filesystem. This function can often be left unimplemented, but it can be a handy way to perform one-time setup such as allocating
    variable-sized data structures or initializing a new filesystem. The
    fuse_conn_info structure gives information about what features are supported
    by FUSE, and can be used to request certain capabilities (see below for more
    information). The return value of this function is available to all file
    operations in the private_data field of fuse_context. It is also passed as a
    parameter to the destroy() method.

    """
    def __init__(self, root):
        self.root = root
        self.files = {}
        self.fd = 0
        self.pw = bytes(getpass("Password: "), encoding="utf-8")

        # Note: The cryptography library stores a hash of the password and
        # throws an Invalid Token Exception when incorrect. This works because
        # the salt generated when a file is closed is dependent upon the
        # provided password.
        
    def destroy(self, path):
        """Clean up any resources used by the filesystem.
        
        Called when the filesystem exits.
        
        """
        pass

    #NOTE THIS MIGHT BE USEFUL IN SEVERAL PLACES!
    def _full_path(self, partial):
        """Calculate full path for the mounted file system.

          .. note::

            This isn't the same as the full path for the underlying file system.
            As such, you can't use os.path.abspath to calculate this, as that
            won't be relative to the mount point root.

        """
        if partial.startswith("/"):
            partial = partial[1:]
        path = os.path.join(self.root, partial)
        return path

    ############################################################################
    ############################################################################
    ############################################################################

    # cryptography library
    # https://cryptography.io/en/latest/fernet/#using-passwords-with-fernet

    def get_cipher_instance(self, salt):

        kdf = PBKDF2HMAC(
            algorithm=hashes.SHA256(),
            length=32,
            salt = salt,
            iterations=100000,
            backend=default_backend()
        )
        key = base64.urlsafe_b64encode(kdf.derive(self.pw))
        return Fernet(key)
    
    def encrypt(self, salt, data):
        f = self.get_cipher_instance(salt)
        return f.encrypt(data)
    
    def decrypt(self, salt, data):
        f = self.get_cipher_instance(salt)
        return f.decrypt(data)

    ############################################################################
    ############################################################################
    ############################################################################

    @logged
    def access(self, path, mode):
        """Access a file.

        This is the same as the access(2) system call. It returns -ENOENT if
        the path doesn't exist, -EACCESS if the requested permission isn't
        available, or 0 for success. Note that it can be called on files,
        directories, or any other object that appears in the filesystem. This
        call is not required but is highly recommended.

        """
        full_path = self._full_path(path)
        if not os.access(full_path, mode):
            raise FuseOSError(errno.EACCES)

    @logged
    def chmod(self, path, mode):
        """Change a file's permissions.

        Change the mode (permissions) of the given object to the given new
        permissions. Only the permissions bits of mode should be examined. See
        chmod(2) for details.

        """
        full_path = self._full_path(path)
        return os.chmod(full_path, mode)

    @logged
    def chown(self, path, uid, gid):
        """Change a file's owernship.


        Change the given object's owner and group to the provided values. See
        chown(2) for details. NOTE: FUSE doesn't deal particularly well with
        file ownership, since it usually runs as an unprivileged user and this
        call is restricted to the superuser. It's often easier to pretend that
        all files are owned by the user who mounted the filesystem, and to skip
        implementing this function.

        """
        full_path = self._full_path(path)
        return os.chown(full_path, uid, gid)

    @logged
    def getattr(self, path, fh=None):
        """Return file attributes.

        The "stat" structure is described in detail in the stat(2) manual page.
        For the given pathname, this should fill in the elements of the "stat"
        structure. If a field is meaningless or semi-meaningless (e.g., st_ino)
        then it should be set to 0 or given a "reasonable" value. This call is
        pretty much required for a usable filesystem.

        """

        # TODO: For FUSE to work correctly, some changes will need to be made 
        # here. For example, changing the st_size to account for the salt.
        # Currently, the salt is included in the length.

        full_path = self._full_path(path)
        st = os.lstat(full_path)
        return dict((key, getattr(st, key)) for key in ('st_atime', 'st_ctime',
               'st_gid', 'st_mode', 'st_mtime', 'st_nlink', 'st_size', 'st_uid'))

    @logged
    def readdir(self, path, fh):
        """Read a directory.

        Return one or more directory entries (struct dirent) to the caller.
        This is one of the most complex FUSE functions. It is related to, but
        not identical to, the readdir(2) and getdents(2) system calls, and the
        readdir(3) library function. Because of its complexity, it is described
        separately below. Required for essentially any filesystem, since it's
        what makes ls and a whole bunch of other things work.

        """
        full_path = self._full_path(path)

        dirents = ['.', '..']
        if os.path.isdir(full_path):
            dirents.extend(os.listdir(full_path))
        for r in dirents:
            yield r

    @logged
    def readlink(self, path):
        """Read a symbolic link.

        If path is a symbolic link, fill buf with its target, up to size. See
        readlink(2) for how to handle a too-small buffer and for error codes.
        Not required if you don't support symbolic links. NOTE: Symbolic-link
        support requires only readlink and symlink. FUSE itself will take care
        of tracking symbolic links in paths, so your path-evaluation code
        doesn't need to worry about it.

        """
        pathname = os.readlink(self._full_path(path))
        if pathname.startswith("/"):
            # Path name is absolute, sanitize it.
            return os.path.relpath(pathname, self.root)
        else:
            return pathname

        #don't allow mknod
    '''@logged
    def mknod(self, path, mode, dev):
        """Make a special file.

        Make a special (device) file, FIFO, or socket. See mknod(2) for
        details. This function is rarely needed, since it's uncommon to make
        these objects inside special-purpose filesystems.

        """
        return os.mknod(self._full_path(path), mode, dev)
'''
    @logged
    def rmdir(self, path):
        """Remove a directory.

        Remove the given directory. This should succeed only if the directory
        is empty (except for "." and ".."). See rmdir(2) for details.

        """
        full_path = self._full_path(path)
        return os.rmdir(full_path)

    @logged
    def mkdir(self, path, mode):
        """Make a directory.

        Create a directory with the given name. The directory permissions are
        encoded in mode. See mkdir(2) for details. This function is needed for
        any reasonable read/write filesystem.

        """
        return os.mkdir(self._full_path(path), mode)

    @logged
    def statfs(self, path):
        full_path = self._full_path(path)
        stv = os.statvfs(full_path)
        return dict((key, getattr(stv, key)) for key in ('f_bavail', 'f_bfree',
          'f_blocks', 'f_bsize', 'f_favail', 'f_ffree', 'f_files', 'f_flag',
          'f_frsize', 'f_namemax'))

    @logged
    def unlink(self, path):
        """Unlink a file.

        Remove (delete) the given file, symbolic link, hard link, or special
        node. Note that if you support hard links, unlink only deletes the data
        when the last hard link is removed. See unlink(2) for details.

        """
        return os.unlink(self._full_path(path))

    #no symlinks
    '''    @logged
    def symlink(self, target, name):
        """Create a symbolic link.

        Create a symbolic link named "from" which, when evaluated, will lead to
        "to". Not required if you don't support symbolic links. NOTE:
        Symbolic-link support requires only readlink and symlink. FUSE itself
        will take care of tracking symbolic links in paths, so your
        path-evaluation code doesn't need to worry about it.

        """
        return os.symlink(self._full_path(target), self._full_path(name))
    '''
    @logged
    def rename(self, old, new):
        """Rename a file.

        Rename the file, directory, or other object "from" to the target "to".
        Note that the source and target don't have to be in the same directory,
        so it may be necessary to move the source to an entirely new directory.
        See rename(2) for full details.

        """
        return os.rename(self._full_path(old), self._full_path(new))
#no hard links either
    '''
    @logged
    def link(self, target, name):
        """Create a hard link.

        Create a hard link between "from" and "to". Hard links aren't required
        for a working filesystem, and many successful filesystems don't support
        them. If you do implement hard links, be aware that they have an effect
        on how unlink works. See link(2) for details.

        """
        return os.link(self._full_path(target), self._full_path(name))
'''
    @logged
    def utimens(self, path, times=None):
        return os.utime(self._full_path(path), times)

    ############################################################################
    ############################################################################
    ############################################################################

    @logged
    def open(self, path, flags=None):
        """Opens a file.

        Opens a file within the virtual filesystem. That is, opens and decrypts a file in the physical filesystem and store the decrypted copy in memory. Returns a file descriptor referencing the copy (as if the physical copy remained open).

        If the file is already open or any other error occurs, returns -1.

        C FUSE API: Open a file. If you aren't using file handles, this function shouldjust check for existence and permissions and return either success or an error code. If you use file handles, you should also allocate any necessary structures and set fi->fh. In addition, fi has some other fields that an advanced filesystem might find useful; see the structure definition in fuse_common.h for very brief commentary.

        """
        # path = self._full_path(path)
        if path in self.files:
            return -1

        # TODO: Q. Files opened via direct syscall outside of this class are 
        # not detected here as "already opened". For example:
        #
        # open("somefile.txt")
        # encfs = EncFS("somePathToEffectiveRoot")
        # encfs.open("somefile.txt")
        #
        # The above code executes without error. Is there a problem in this 
        # scenario?

        try:
            f = open(path, "rb") # physical filesystem
        except OSError:

            # TODO: Q. It is assumed here the only errors will result from file
            # incorrect file permissions or non-existent files. However, the
            # documentation does not specify the cause of errors that might be
            # thrown. Is it possible to be sure of the possible causes?

            return -1
        else:

            salt = f.read(16)
            ciphertext = f.read()
            f.close()

            plaintext = self.decrypt(salt, ciphertext)

            # Note: The purpose and necessity of the file descriptor is unclear 
            # in context of the FUSE API.

            self.files[path] = plaintext
            fd = self.fd
            self.fd += 1
            return fd
        
    @logged
    def create(self, path, mode=None, fi=None):
        """Create an empty file.

        Create an empty file in both the virtual and the physical filesystems. Return a file descriptor referencing the existence of a virtual copy. The physical copy is immediately closed.

        """
        
        # path = self._full_path(path)

        # TODO: Q. The documentation does not describe the reasons for an 
        # OSError. In a case such as this, how is it possible to know if it is
        # necessary to catch such errors?

        try:
            f = open(path, "a").close() #need to assign a salt here?
        except OSError:
            return -1
        else:

            # Note: The purpose and necessity of the file descriptor is unclear 
            # in context of the FUSE API.

            self.files[path] = bytearray("", "utf8")
            fd = self.fd
            self.fd += 1
            return fd

    @logged
    def read(self, path, length, offset, fh=None):
        """Read from a file.

        Read size bytes from the given file into the buffer buf, beginning
        offset bytes into the file. See read(2) for full details. Returns the
        number of bytes transferred, or 0 if offset was at or beyond the end of
        the file. Required for any sensible filesystem.

        """

        # TODO: Q. Which is more reliable: returning an error code as an integer
        # or raising an Exception? For example, while testing the truncate
        # method, attempting to retrieve the length for a failed read results in
        # an unintuitive TypeError because "'int' has no len()".

        # path = self._full_path(path)
        if path not in self.files:
            return -1

        data = self.files[path]
        # Note: For lists containiner fewer items than requested, Python returns
        # an empty list instead of an error.
        return bytes(data[offset:offset + length]) # TODO: bytes?

    @logged
    def write(self, path, buf, offset, fh=None):
        """Write to a file.

        """

        # path = self._full_path(path)

        # TODO: Q. Does FUSE convert buf to a bytearray or bytes object before
        # calling this function? If so, remove the bytearray conversion of buf.
    
        data = bytearray(self.files[path])
        data[offset:offset] = bytearray(buf, "utf8")
        self.files[path] = data

        # TODO: Q. Why would the number of bytes written ever be less than
        # requested?

        return len(buf)

    @logged
    def truncate(self, path, length, fh=None):
        """Truncate a file.

        Truncate or extend the given file so that it is precisely size bytes
        long. See truncate(2) for details. This call is required for read/write
        filesystems, because recreating a file will first truncate it.

        """

        # path = self._full_path(path)

        data = bytearray(self.files[path]) # bytearray is mutable
        size = len(data)
        if size < length:
            data[size:length] = [0] * (length - size)
        else:
            data = data[:length]
        self.files[path] = data

        # TODO: Q1. What should happen for negative lengths? A negative value
        # inside the splice method will result in operating from the opposite 
        # end. Should an error be thrown? Q2. What is returned?

        return

    ############################################################################
    ############################################################################
    ############################################################################

    #skip
    '''
    @logged
    def flush(self, path, fh):
        """Flush buffered information.

        Called on each close so that the filesystem has a chance to report
        delayed errors. Important: there may be more than one flush call for
        each open. Note: There is no guarantee that flush will ever be called
        at all!

        """
        return os.fsync(fh)
   '''                                            

    ############################################################################
    ############################################################################
    ############################################################################

    @logged
    def release(self, path, fh=None):
        """Release is called when FUSE is done with a file.

        This is the only FUSE function that doesn't have a directly
        corresponding system call, although close(2) is related. Release is
        called when FUSE is completely done with a file; at that point, you can
        free up any temporarily allocated data structures. The IBM document
        claims that there is exactly one release per open, but I don't know if
        that is true.

        """

        # path = self._full_path(path)
        
        # TODO: Q. Requirement to explicitly open the file before calling write?

        try:
            f = open(path, "wb")
        except OSError:

            # TODO: Q. What should happen if the system encounters an error 
            # here? This would mean the resources are not freed and no new salt 
            # is written to the encrypted physical FS...

            return -1
        else:

            # TODO: Remove the entry from self.files

            salt = os.urandom(16)
            plaintext = bytes(self.files[path]) # encryption requires bytes
            self.files.pop(path)

            ciphertext = self.encrypt(salt, plaintext)

            f.write(salt)
            f.write(ciphertext)

        return 'FILL ME IN'

    ############################################################################
    ############################################################################
    ############################################################################
    
    #skip
'''    @logged
    def fsync(self, path, fdatasync, fh):
        """Flush any dirty information to disk.

        Flush any dirty information about the file to disk. If isdatasync is
        nonzero, only data, not metadata, needs to be flushed. When this call
        returns, all file data should be on stable storage. Many filesystems
        leave this call unimplemented, although technically that's a Bad Thing
        since it risks losing data. If you store your filesystem inside a plain
        file on another filesystem, you can implement this by calling fsync(2)
        on that file, which will flush too much data (slowing performance) but
        achieve the desired guarantee.

        """
        return self.flush(path, fh)
'''

    ############################################################################
    ############################################################################
    ############################################################################

def runtests(path):
    print("\n\n----------RUNNING_UNIT_TESTS----------")
    encfs = EncFS("someRoot") # this is passed to FUSE and is not used here
    print() # newline after password prompt

    testOpen(encfs, path)
    testCreate(encfs, path)
    testRead(encfs, path)
    testWrite(encfs, path)
    testTruncate(encfs, path)
    testRelease(encfs, path)

    print("\n----------TERMINATING_UNIT_TESTS: SUCCESS----------\n")

def testOpen(encfs, path):
    path = path + "hello"
    length = 200 # arbitrary value greater than length of test file
    offset = 0

    fd1 = encfs.open(path)
    fd2 = encfs.open(path)

    assert(fd1 == 0), "next available file descriptor"
    assert(fd2 == -1), "file already open"
    assert(len(encfs.files) == 1), "number of files open"

    encfs.files.pop(path) # remove for remaining tests
    print("SUCCESS: OPEN")

def testCreate(encfs, path):
    path = path + "create.txt"
    length = 200 # arbitrary value greater than length of test file
    offset = 0

    # The path will exist on the physical filesystem after a single execution
    # of this method. The try block is used to prevent an invalid token when
    # the call to encfs.open is made and the file exists but without a salt.
    # This occurs because these tests are run independent of FUSE and the
    # release method is not called. Normally, release would assign a readable
    # salt to the specified file.
    
    fileExists = True

    try:
        open(path)
    except FileNotFoundError:
        fileExists = False
        fd = encfs.open(path)
        assert(fd == -1), "file should not exist"
    
    fd = encfs.create(path)
    assert(fd >= 0), "file should have been created"
    fd = encfs.open(path)
    assert(fd == -1), "file should exist as empty dictionary entry"

    encfs.files.pop(path) # remove for remaining tests
    print("SUCCESS: CREATE")

def testRead(encfs, path):
    path = path + "hello"
    length = 200 # arbitrary value greater than length of test file
    offset = 0

    fd = encfs.open(path)
    assert(fd >= 0), "file should exist"
    data1 = encfs.read(path, length, offset)
    assert(data1 != -1), "see TODO in read"

    offset = 3
    data2 = encfs.read(path, length, offset)
    assert(len(data2) == len(data1) - offset)

    encfs.files.pop(path)
    
    print("SUCCESS: READ\n")
    print(data1)
    print(data2)

def testWrite(encfs, path):
    path = path + "write.txt"
    length = 200 # arbitrary value greater than length of test file
    offset = 0
    buf1 = "writing to file"

    fd = encfs.create(path)
    assert(fd >= 0), "file should exist"
    bytesWritten = encfs.write(path, buf1, offset)
    assert(bytesWritten == len(buf1))
    data1 = encfs.read(path, length, 0)

    offset = int(bytesWritten / 2)
    buf2 = " \"inserted offset\""
    bytesWritten = encfs.write(path, buf2, offset)
    assert(bytesWritten == len(buf2))
    data2 = encfs.read(path, length, 0)
    
    # TODO: Add assert statements to ensure the offset insertions are placed
    # correctly (rather than relying on the following print statements)

    print("\nSUCCESS: WRITE\n")
    print(data1)
    print(data2)

def testTruncate(encfs, path):
    path = path + "truncate.txt"
    length = 5 # arbitrary value greater than length of test file
    offset = 0
    
    fd = encfs.create(path)
    assert(fd >= 0)
    data1 = encfs.read(path, length, offset)
    assert(len(data1) == 0)

    encfs.truncate(path, length)
    data2 = encfs.read(path, length, offset)
    assert(len(data2) == length)

    length = 50
    buf = "writing truncate test"
    encfs.write(path, buf, offset)
    data3 = encfs.read(path, length, offset)
    assert(len(data3) == len(data2) + len(buf))

    print("\nSUCCESS: TRUNCATE\n")
    print(data1)
    print(data2)
    print(data3)

    # TODO: Test negative lengths. Would it ever be negative?

def testRelease(encfs, path):

    path = path + "release.txt"
    length = 200 # arbitrary value
    offset = 0
    buf = "Hello, World!"

    fd = encfs.create(path)
    assert(fd >= 0)
    encfs.write(path, buf, offset)
    data1 = encfs.read(path, length, offset)
    assert(len(data1) == len(buf))

    encfs.release(path)
    fd = encfs.read(path, length, offset)
    assert(fd == -1), "file should be closed after release"

    fd = encfs.open(path)
    assert(fd >= 0)
    data2 = encfs.read(path, length, offset)
    assert(len(data2) == len(data1))

    print("\nSUCCESS: RELEASE\n")
    print(data1)
    print(data2)

if __name__ == '__main__':
    from sys import argv
    if len(argv) != 3:
        print('usage: %s <encrypted folder> <mountpoint>' % argv[0])
        exit(1)
    if len(argv) == 3 and argv[1] == "test":
        runtests(argv[2])
        exit(1)

    logging.basicConfig(level=logging.DEBUG)
    #create our virtual filesystem using argv[1] as the physical filesystem
    #and argv[2] as the virtual filesystem
    fuse = FUSE(EncFS(argv[1]), argv[2], foreground=True)

