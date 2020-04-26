import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tree{
    private int valueTab = 0;
    private static final String FILENAME = "structure.txt";
    private static final String DATAFILE = "suffix.dat";
    private List<String> suffix = new ArrayList<>();

    public static void main(String[] args) {
        Tree tree = new Tree();
        if (args.length == 1){
            File dir = new File(args[0]);
            if (dir.exists() && dir.isDirectory()){
                File file = new File(dir + File.separator + FILENAME);
                tree.writeFile(tree, file, dir);
            }
            else if (dir.isFile()){
                tree.readFile(dir);
            }
        }
    }

    private void writeFile (Tree tree, File file, File dir){
        try(PrintWriter printWriter = new PrintWriter(new BufferedWriter(new PrintWriter(file)))) {
            for (File currentDir: Objects.requireNonNull(dir.listFiles())
            ) {
                if (currentDir.isFile()){
                    lookingForSuffix(tree, currentDir.getName());
                }
                printWriter.println(currentDir.getName());
                tree.lookingAddingFiles(tree, currentDir, printWriter);
            }
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(dir + File.separator + DATAFILE));
            outputStream.writeObject(tree.suffix);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile (File dir){
        int countFiles = 0;
        int countFolders = 0;
        long averageLengthFileName = 0L;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(dir.getParent() + File.separator + "suffix.dat"));
             FileReader fileReader = new FileReader(dir);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            ArrayList <String> suffix = new ArrayList<String>(((List) inputStream.readObject()));
            while (bufferedReader.ready()){
                String currentLine = bufferedReader.readLine();
                int index = currentLine.indexOf('.');
                if (index != -1 && suffix.contains(currentLine.substring(index).toLowerCase())){
                    averageLengthFileName += currentLine.substring(index).length();
                    countFiles++;
                }
                else {
                    countFolders++;
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("* Количество папок: " + countFolders);
        System.out.println("* Количество файлов: " + countFiles);
        System.out.printf("* Среднее количество файлов в папке: %.2f %n", (float)countFiles / countFolders);
        System.out.printf("* Среднюю длинну названия файла: %.2f", (float) averageLengthFileName / countFiles);
    }

    private void lookingAddingFiles(Tree tree, File file, PrintWriter pw){
        if (file.exists() && file.isDirectory()){
            for (File currentFile: Objects.requireNonNull(file.listFiles())){
                if (currentFile.isFile()){
                    lookingForSuffix(tree, currentFile.getName());
                }
                pw.println("└───" + currentFile.getName());
                tree.lookingAddingFilesTab(tree, currentFile, pw);
            }
        }
    }

    private void lookingAddingFilesTab(Tree tree, File file, PrintWriter pw){
        if (file.exists() && file.isDirectory()) {
            valueTab++;
            for (File currentFile : Objects.requireNonNull(file.listFiles())) {
                for (int i = 0; i < valueTab; i++){
                    pw.print("\t");
                }
                if (currentFile.isFile()){
                    lookingForSuffix(tree, currentFile.getName());
                }
                pw.println("└───" + currentFile.getName());
                tree.lookingAddingFilesTab(tree, currentFile, pw);
            }
            valueTab = 0;
        }
    }

    private static void lookingForSuffix (Tree tree, String suffix){
        String currentSuffix = suffix;
        int index = currentSuffix.indexOf('.');
        if (index != -1){
            currentSuffix = currentSuffix.substring(index).toLowerCase();
            if(!tree.suffix.contains(currentSuffix)){
                tree.suffix.add(currentSuffix);
            }
        }
    }
}