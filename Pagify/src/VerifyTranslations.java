import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class VerifyTranslations {

  public static void main(String[] args) throws IOException {
    Server server = new Server("../resources/songList.bin", 50, new
            RandomPolicy());
    int playlistQuantity = 5;
    int songsPerPlaylist = 10;
    ArrayList<Playlist> playlists = createPlaylists(server, playlistQuantity,
            songsPerPlaylist);
    printPlaylists(playlists, server, songsPerPlaylist);
  }

  public static ArrayList<Playlist> createPlaylists(Server server, int
          playlistQuantity, int songsPerPlaylist) throws IOException {
    ArrayList<Playlist> playlists = new ArrayList<>();
    for (int i = 0; i < playlistQuantity; i++) {
      Playlist playlist = server.makePlaylist();
      for (int j = 0; j < songsPerPlaylist; j++) {
        server.addSong(playlist);
      }
      playlists.add(playlist);
    }
    return playlists;
  }

  public static void printPlaylists(ArrayList<Playlist> playlists, Server
          server, int songsPerPlaylist) throws IOException {
    for (int i = 0; i < playlists.size(); i++) {
      Playlist playlist = playlists.get(i);
      System.out.println("playlist id: " + playlist.getId());
      for (int j = 0; j < playlist.getSongCount(); j++) {
        System.out.println("song " + j + ": " + playlist.getSong(j, server)
                .name);
      }
      System.out.println("memory hit count: " + server.getHitCount());
      System.out.println();
    }
  }
}
