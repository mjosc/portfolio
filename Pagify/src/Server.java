import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Random;

public class Server {

  private Song[] songList;
  private RandomAccessFile raf;
  // HashMap<playlistId, HashMap<virtualAddress, physicalAddress>>
  private HashMap<Integer, HashMap<Integer, Integer>> pageTable;
  private int nextId; // Next playlist id
  private Random rand;
  private int songsInMemory;
  private int maxSongsInMemory;
  private SwapPolicy swapPolicy;
  private int hitCount;

  public Server(String filename, int maxSongsInMemory, SwapPolicy swapPolicy)
          throws IOException {
    raf = new RandomAccessFile(filename, "r");
    songList = new Song[readSongCount(raf)]; // All song objects initialized
    // to null
    pageTable = new HashMap<>();
    nextId = 0;
    rand = new Random();
    songsInMemory = 0;
    this.maxSongsInMemory = maxSongsInMemory;
    this.swapPolicy = swapPolicy;
  }

  public Playlist makePlaylist() {
    Playlist playlist = new Playlist(nextId++);
    pageTable.put(playlist.getId(), new HashMap<Integer, Integer>());
    return playlist;
  }

  public void addSong(Playlist playlist) {
    playlist.allocateSong(); // Avoid case where virtual address = -1
    // Exclusive upper random bound guarantees no address at songList.length
    int physicalAddress = rand.nextInt(songList.length);
    int virtualAddress = playlist.getSongCount() - 1;
    pageTable.get(playlist.getId()).put(virtualAddress, physicalAddress);
  }

  private int translateAddress(int playlistId, int virtualAddress) {
    return pageTable.get(playlistId).get(virtualAddress);
  }

  public Song getSong(int playlistId, int virtualAddress) throws IOException {
    int physicalAddress = translateAddress(playlistId, virtualAddress);
    if (songList[physicalAddress] == null) {
      if (songsInMemory >= maxSongsInMemory) {
        songList[swapPolicy.whichPageShouldBeEvicted()] = null;
      } else {
        // Just load new song
        songsInMemory++;
      }
      loadSong(physicalAddress);
      assert songsInMemory <= maxSongsInMemory;
    } else {
      //System.out.println("hit");
      hitCount++; // Song was already in memory
    }
    swapPolicy.pageAccessed(physicalAddress);
    assert (songList[physicalAddress] != null);
    return songList[physicalAddress];
  }

  private void loadSong(int physicalAddress) throws IOException {
    raf.seek(4 + (256 * physicalAddress));
    int len = raf.readByte(); // Song title at first byte
    byte[] buf = new byte[len];
    raf.readFully(buf);
    songList[physicalAddress] = new Song(new String(buf));
  }

  private int readSongCount(RandomAccessFile raf) throws
          IOException {
    raf.seek(0);
    byte[] buf = new byte[4];
    raf.readFully(buf);
    return ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
  }

  public int getHitCount() {
    return hitCount;
  }

  // Creates a playlist where the virtual address of each song  = physical address
  public Playlist createFullPlaylist() {
    Playlist playlist = makePlaylist();
    for (int i = 0; i < songList.length; i++) {
      playlist.allocateSong();
      int virtualAddress = playlist.getSongCount() - 1;
      int physicalAddress = virtualAddress;
      pageTable.get(playlist.getId()).put(virtualAddress, physicalAddress);
    }
    return playlist;
  }

}