import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Evaluate {

  public static void main(String[] args) throws IOException {

    String filename = args[0];
    int workloadLength = 1000;
    int numSongs = 100;

    FileOutputStream fileOutputStream1 = new FileOutputStream
            ("../resources/randomPolicy");
    FileOutputStream fileOutputStream2 = new FileOutputStream
            ("../resources/lruPolicy");
    FileOutputStream fileOutputStream3 = new FileOutputStream
            ("../resources/optimalPolicy");

    PrintWriter printWriter1 = new PrintWriter(fileOutputStream1);
    PrintWriter printWriter2 = new PrintWriter(fileOutputStream2);
    PrintWriter printWriter3 = new PrintWriter(fileOutputStream3);

    printWriter1.println("RandomPolicy\nMemory Size vs Hit Rate");
    printWriter2.println("LRUPolicy\nMemory Size vs Hit Rate");
    printWriter3.println("OptimalPolicy\nMemory Size vs Hit Rate");

    for (int songsInMemory = 5; songsInMemory < 101; songsInMemory += 5) {
      ArrayList<Integer> workload = Workloads.get8020Workload
              (numSongs, workloadLength);

      Server server1 = new Server(filename, songsInMemory, new RandomPolicy());
      Playlist playlist1 = server1.createFullPlaylist();
      Server server2 = new Server(filename, songsInMemory, new LRUPolicy());
      Playlist playlist2 = server2.createFullPlaylist();
      Server server3 = new Server(filename, songsInMemory, new OptimalPolicy
              (workload));
      Playlist playlist3 = server3.createFullPlaylist();

      for (int song = 0; song < workload.size(); song++) {
        playlist1.getSong(workload.get(song), server1);
        playlist2.getSong(workload.get(song), server2);
        playlist3.getSong(workload.get(song), server3);
      }

      printWriter1.println(songsInMemory + "\t" + (((double)
              server1.getHitCount() / workloadLength) * 100));
      printWriter2.println(songsInMemory + "\t" + (((double)
              server2.getHitCount() / workloadLength) * 100));
      printWriter3.println(songsInMemory + "\t" + (((double)
              server3.getHitCount() / workloadLength) * 100));
    }

    printWriter1.flush();
    printWriter2.flush();
    printWriter3.flush();
  }

}
