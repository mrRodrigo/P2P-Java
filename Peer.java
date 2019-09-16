import java.util.HashMap;
import java.nio.file.*;
import java.io.IOException;
import java.util.stream.Stream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;
import java.io.Serializable;
import java.net.*;
import java.io.*;

public class Peer implements Serializable {
  private static final long serialVersionUID = 1L;

  public String name; // client name
  public String address; // client address
  public HashMap<String, String> files; // array for all hash and path files

  public Peer(String[] args) {
    name = args[1];
    address = args[2];
    files = new HashMap<String, String>();
    saveAllFilesPath("./share");
  }

  public void requestFile(String hashFile, Peer host) throws IOException {
    String pathFile = pathByHash(hashFile);

    Socket socket = new Socket(host.getAddress(), 4444);
    DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

    // escreve 1, para dizer que é uma requisição de arquivo
    dOut.writeByte(1);
    // escreve o caminho do arquivo que quero
    dOut.writeUTF(pathFile);
    dOut.flush(); // Send off the data

    // encerra
    dOut.writeByte(-1);
    dOut.flush(); // Send off the data

    dOut.close();

  }

  public void saveAllFilesPath(String rootPath) {
    System.out.println("Reading your files to share...");
    try (Stream<Path> paths = Files.walk(Paths.get(rootPath))) {

      paths.forEach(filePath -> {
        if (Files.isRegularFile(filePath)) {
          try {
            files.put(getHashFile(filePath.toString()), filePath.toString());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
      System.out.println("Files ready");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getHashFile(String path) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(Files.readAllBytes(Paths.get(path)));

      String checksum = new BigInteger(1, md.digest()).toString(16);

      return checksum;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean haveFileByHash(String hash) {
    for (String fileHash : files.keySet()) {
      if (fileHash.equals(hash)) {
        return true;
      }
    }
    return false;
  }

  public String pathByHash(String hash) {
    for (String fileHash : files.keySet()) {
      if (fileHash.equals(hash)) {
        return files.get(fileHash);
      }
    }
    return null;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public Map<String, String> getFiles() {
    return files;
  }

}
