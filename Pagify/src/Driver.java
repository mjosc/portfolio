import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Driver {

  public static void main(String[] args) throws IOException {
    ArrayList<Integer> workload = new ArrayList<>(Arrays.asList(0, 1, 2, 0,
            1, 3, 0, 3, 1, 2, 1));
    Server server = new Server("../resources/songList.bin", 3, new
            OptimalPolicy(workload));
    Playlist playlist = server.makePlaylist();
    assert playlist.getId() == 0;
    for (int i = 0; i < 4; i++) {
      server.addSong(playlist);
      assert playlist.getSongCount() == i + 1;
    }
    for (int i = 0; i < workload.size(); i++) {
      playlist.getSong(workload.get(i), server);
    }
  }
}

//  Evaluate the choice of swapping policy on the 3 classic workloads:  an
// 80/20 workload which is provided in the Workloads, a random workload, and
// a looping workload.  Compute and graph the hit rate of the the combinations
// of swapper/workload when increasing the number of songs that are allowed
// to be in memory at the same time.

