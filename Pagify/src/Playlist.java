import java.io.IOException;

public class Playlist {

  private int songCount;
  private int playlistId;

  public Playlist(int playlistId) {
    songCount = 0;
    this.playlistId = playlistId;
  }

  public void allocateSong() {
    songCount++;
  }

  public Song getSong(int virtualAddress, Server server) throws IOException {
    return server.getSong(playlistId, virtualAddress);
  }

  public int getSongCount() {
    return songCount;
  }

  public int getId() {
    return playlistId;
  }
}

