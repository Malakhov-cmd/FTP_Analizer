import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Processing {
    public static ArrayList<NodeSrc> directoryName = new ArrayList<>();
    public static StringBuffer stringBuilderSystemAnswer = new StringBuffer();

    public static ArrayList<NodeSrc> getDirectoryNameList() {
        return directoryName;
    }

    public static void cleanList() {
        directoryName = new ArrayList<>();
    }

    public static class NodeSrc {
        private int level;
        private String name;
        int type;
        long size;
        String user;
        String rawListing;
        int hardLinkCount;

        public NodeSrc(int level, String name) {
            this.level = level;
            this.name = name;
        }

        public NodeSrc(int level, String name, int type, long size, String user, String rawListing, int hardLinkCount) {
            this.level = level;
            this.name = name;
            this.type = type;
            this.size = size;
            this.user = user;
            this.rawListing = rawListing;
            this.hardLinkCount = hardLinkCount;
        }

        public int getType() {
            return type;
        }

        public long getSize() {
            return size;
        }

        public String getUser() {
            return user;
        }

        public String getRawListing() {
            return rawListing;
        }

        public int getHardLinkCount() {
            return hardLinkCount;
        }

        public int getLevel() {
            return this.level;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static void listDirectory(FTPClient ftpClient, String parentDir, String currentDir, int level) throws IOException {
        String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }
        FTPFile[] subFiles = ftpClient.listFiles(dirToList);
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    continue;
                }
                for (int i = 0; i < level; i++) {
                    System.out.print("\t");
                }
                if (aFile.isDirectory()) {
                    System.out.println("[" + currentFileName + "]");
                    int type = aFile.getType();
                    long size = aFile.getSize();
                    String user = aFile.getUser();
                    String rawListing = aFile.getRawListing();
                    int hardLinkCount = aFile.getHardLinkCount();
                    directoryName.add(new NodeSrc(level, currentFileName, type, size, user, rawListing, hardLinkCount));
                    listDirectory(ftpClient, dirToList, currentFileName, level + 1);
                } else {
                    System.out.println(currentFileName);
                }
            }
        }
    }

    public static void ftpInfo() throws IOException {
        String server = "91.222.128.11";
        int port = 21;
        String user = "testftp_guest";
        String pass = "12345";

        FTPClient ftpClient = new FTPClient();

        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(new FileWriter("textConsole.txt"))));

        try {
            ftpClient.connect(server, port);
            showServerReply(ftpClient);

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                throw new IOException("Exception in connecting to FTP Server");
            }
            boolean success = false;
            success = ftpClient.login(user, pass);

            showServerReply(ftpClient);
            if (!success) {
                System.out.println("Could not login to the server");
                return;
            }

            if (ftpClient.isConnected()) {

                ftpClient.enterLocalPassiveMode();

                directoryName.add(new NodeSrc(0, "/"));
                listDirectory(ftpClient, "", "", 1);
                for (NodeSrc src : directoryName) {
                    System.out.println(src.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


    }

    public static void ftpInfo(String server, String port, String user, String pass) throws IOException {

        FTPClient ftpClient = new FTPClient();

        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(new FileWriter("textConsole.txt"))));

        try {
            int portFormated = Integer.parseInt(port);
            ftpClient.connect(server, portFormated);
            showServerReply(ftpClient);

            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                throw new IOException("Exception in connecting to FTP Server");
            }
            boolean success = false;
            success = ftpClient.login(user, pass);

            showServerReply(ftpClient);
            if (!success) {
                System.out.println("Could not login to the server");
                return;
            }

            if (ftpClient.isConnected()) {

                ftpClient.enterLocalPassiveMode();

                directoryName.add(new NodeSrc(0, "/"));
                listDirectory(ftpClient, "", "", 1);
                for (NodeSrc src : directoryName) {
                    System.out.println(src.toString());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


    }

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }

}
