import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Walker {
    static public String getPath() throws IOException {
        return new File(".").getCanonicalPath();
    }

    static public Set<String> getFileList(String dir) {

        return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getAbsolutePath)
                .collect(Collectors.toSet());
    }

    static public Set<String> getDirList(String dir) {

        return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
                .filter(File::isDirectory)
                .map(File::getAbsolutePath)
                .collect(Collectors.toSet());
    }

    static public Set<String> Walk(String dir) {
        System.out.println("[*] scanning " + dir);
        Set<String> allFiles = getFileList(dir);
        Set<String> allDirs = getDirList(dir);

        ConcurrentLinkedDeque<String> mapped =
                new ConcurrentLinkedDeque<String>(allDirs);

        String next = "\0";
        while (mapped.size() > 0) {
            next = mapped.iterator().next();
            allFiles.addAll(getFileList(next));
            mapped.addAll(getDirList(next));
            mapped.remove(next);
            System.out.println("[*] scanning " + next);
        }
        return allFiles;
    }

    static public String SHA256(String fileName)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try {
            var file = new FileInputStream(fileName);
            while( file.available() > 0) {
                byte[] buffer = file.readNBytes(2097152);
                md.update(buffer);
            }return String.format("%x",
                    new BigInteger(1, md.digest()));
        } catch (Exception e) {
            System.out.println("[*] ERROR" + e);
        }
        return "Hashing failed.";
    }

    public static class Hashmaykr {
        public final HashMap<String, String> HashList =
                new HashMap<String, String>();

        public Hashmaykr(Set<String> filesToHash)
                throws NoSuchAlgorithmException, IOException {
            for (String s2hash : filesToHash) {
                this.HashList.put(s2hash, Walker.SHA256(s2hash));
            }
        }

        public long countOcc(String hash){
            return Collections.frequency(this.HashList.values(), hash);
        }

        public void showAll(){
            this.HashList.forEach((k,v)
                    -> System.out.println(k + " : " + v));;
        }

        public String getHash(String file) {
            return this.HashList.get(file);
        }

        public HashMap<String, String> giveAll() {
            return this.HashList;
        }

        public String getFile(String hash) {
            if (this.HashList.containsValue(hash)){
                    for (Map.Entry<String, String> entry : HashList.entrySet())
                    {
                            var val = entry.getValue();
                            if (Objects.equals(val, hash))
                                return entry.getKey();
                    }
            } else System.out.println("[*] File not in HashList! " + hash);
            return null;
        }

        public List<String> getFiles(String hash) {
            ArrayList<String> results = new ArrayList<String>();
            if (this.HashList.containsValue(hash)){
                for (Map.Entry<String, String> entry : HashList.entrySet())
                {
                    var val = entry.getValue();
                    if (Objects.equals(val, hash)) {
                        results.add(entry.getKey());
                    }
                }
            } else System.out.println("[*] File not in HashList! " + hash);
            return results;
        }

        public List<String> findSingleDupes(String hash){
//            return this.HashList.values().stream()
//                    .filter(h -> h.equals(hash))
//                    .map(h -> new String(this.getFile(h)))
//                    .collect(Collectors.toList());
            ArrayList<String> results = new ArrayList<String>();
            if (this.countOcc(hash) > 1)
            {
                for (Map.Entry<String, String> entry : HashList.entrySet()) {
                    if (entry.getValue().equals(hash)) {
                        results.add(entry.getKey());
                    }
                }
            }
            return results;
        }

        public ArrayList<List<String>> findAllFileDupes()
        {
            ArrayList<List<String>> results = new ArrayList<>();
            for (String hash : HashList.values())
            {
                List<String> chunk = findSingleDupes(hash);
                if(!chunk.isEmpty() && !results.contains(chunk))
                    results.add(chunk);
            }
            return results;
        }
    }
}


