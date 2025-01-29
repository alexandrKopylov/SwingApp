import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class GetFilesFromARM extends JFrame {
    // na rabote
//    File fileCSV = new File("c:\\Users\\alexx\\Desktop\\ДеталиБК_все.csv");
//    Path pathBazaDXF = Path.of("z:\\BAZA\\DXF");
//    Path folderMyDXF = Path.of("C:\\Program Files\\AutoCAD 2010\\_DXF\\");
//    Path saveText = Path.of("C:\\Users\\alexx\\Desktop\\Swing");
//    Path folderPDF = Path.of("C:\\Users\\alexx\\Desktop\\Swing\\PDF");
//    Path folderMosinPlusFolderZakaz;

    // doma
    File fileCSV = new File("C:\\Users\\user\\Desktop\\work\\work\\ДеталиБК_все.csv");   //"c:\\Users\\alexx\\Desktop\\ДеталиБК_все.csv");
    Path pathBazaDXF = Path.of("C:\\Users\\user\\Desktop\\work\\work\\BAZA");          //"z:\\BAZA\\DXF");
    Path folderMyDXF = Path.of("C:\\Users\\user\\Desktop\\Swing\\DXFprogramfiles");         //"C:\\Program Files\\AutoCAD 2010\\_DXF\\");
    Path saveText = Path.of("C:\\Users\\user\\Desktop\\Swing");
    Path folderPDF = Path.of("C:\\Users\\user\\Desktop\\Swing\\PDF");
    Path folderMosinPlusFolderZakaz;

    Predicate<Poziciya> filterNOTFILE;
    Predicate<Poziciya> filterThickness;
    Predicate<Poziciya> filterInv;
    Predicate<Poziciya> filterMore;
    Predicate<Poziciya> filterContains;

    List<Poziciya> list = new ArrayList<>();
    List<Poziciya> filterListPoz;
    List<String> listPrintPoz;
    int flagViewNull;
    int count;

    private int maxLengthFileName;
    private int maxLenghtINV;
    private int maxLenghtName;

    private JPanel mainPanel;
    private JPanel westPanel;
    private JPanel centerPanel;
    private JCheckBox checkBoxOnlySelectedMashine;
    private JComboBox<String> getMachine;
    private JTextArea textArea1;
    private JButton readCsvButton;
    private JComboBox<String> getThickness;
    private JButton searchDXFButton;
    private JButton saveInFileTXT;
    private JButton filesInDXFfolderButton;
    private JComboBox<String> invList;
    private JButton notFilesButton;
    private JCheckBox pozCheckBox;
    private JTextField mosinTextField;
    private JCheckBox moreCheckBox;
    private JTextField moreTextField;
    private JCheckBox containsCheckBox;
    private JTextField containsTextField;
    private JCheckBox hideNOTFILECheckBox;
    private JButton NULLOnOfflButton;
    private List<String> thickness;
    private List<String> tmpInvList;
    private static final String[] mashines = {"F", "F12", "KF", "L"};
    private Set<String> thicknessSet = new TreeSet<>();
    private Set<String> invSet = new TreeSet<>();


    //construktor
    public GetFilesFromARM() {
        filterNOTFILE = n -> {
            if (hideNOTFILECheckBox.isSelected()) {
                return !n.getFileName().equals("NOT FILE");
            }
            return true;
        };
        filterMore = n -> {
            if (moreCheckBox.isSelected()) {
                String[] strMas = moreTextField.getText().split(" ");
                int sum = n.getCountN() + n.getCountT();
                if (strMas[0].equals("!")) {
                    return !(sum >= Integer.parseInt(strMas[1]));
                } else {
                    return sum >= Integer.parseInt(strMas[0]);
                }
            }
            return true;
        };
        filterContains = n -> {
            if (containsCheckBox.isSelected()) {
                String[] strMas = containsTextField.getText().split(" ");
                if (strMas[0].equals("!")) {
                    return !n.getName().contains(strMas[1]);
                } else {
                    return n.getName().contains(strMas[0]);
                }
            }
            return true;
        };
        filterThickness = n -> {
            if (getThickness.getSelectedIndex() == 0) {
                return true;
            }
            return n.getGabariti().startsWith(thickness.get(getThickness.getSelectedIndex()));
        };
        filterInv = n -> {
            if (invList.getSelectedIndex() == 0) {
                return true;
            }
            return n.getInv().equals(tmpInvList.get(invList.getSelectedIndex()));
        };

        readCsvButton.addActionListener(e -> {

            textArea1.setText("");

            // если список с поз пуст, то тогда читаем csv - файл
            if (list.isEmpty()) {
                readCSV(fileCSV);

                // из csv-файла , также выделяем толщины и инвентарный, чтобы заполнить выпадающие списки
                if (thickness == null) {
                    thickness = new ArrayList<>(thicknessSet);
                    thickness.add(0, "All");
                    thickness.forEach(x -> getThickness.addItem(x));

                    tmpInvList = new ArrayList<>(invSet);
                    tmpInvList.add(0, "All");
                    tmpInvList.forEach(y -> invList.addItem(y));
                }
            }
            ViewCSV();
        });

        searchDXFButton.addActionListener(e -> {
            // obnulyaem list
            for (Poziciya p : list) {
                p.setStatus("---");
                p.setGeomMashines(null);
                p.setAbslutFileName(null);
                if (p.getFlagDXF() == null) {
                    p.setFileName("NOT FILE");
                }
            }
            maxLengthFileName = 0;
            filterListPoz.forEach(this::searchFiles);
            viewSearchFiles();
        });

        saveInFileTXT.addActionListener(e -> saveInFile());

        filesInDXFfolderButton.addActionListener(e -> copyFilesInDXFfolder());

        notFilesButton.addActionListener(e -> {

            if (mosinTextField.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "папка для поиска не задана");
            } else {
                serchPDF();
                searchDXF();
                viewSearchFiles();
            }
        });


        NULLOnOfflButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (flagViewNull == 0) {
                    textArea1.setText("");
                    for (Poziciya poz : filterListPoz) {
                        if (poz.getFlagDXF() == null) {
                            textArea1.append(viewPoziciya(poz));
                        }
                    }
                    flagViewNull = 1;
                } else {
                    viewSearchFiles();
                    flagViewNull = 0;
                }


            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("GetFilesFromARM");
        frame.setContentPane(new GetFilesFromARM().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setSize(1200, 600);
        frame.setLocation(150, 50);
        frame.setVisible(true);
    }

    private void searchDXF() {

        Predicate<Path> filterPoziciya = n -> {
            if (pozCheckBox.isSelected()) {
                return true;
            }
            return !Character.isDigit(n.toFile().getName().charAt(0));
        };

        List<Path> listPathDXF;

        try {
            listPathDXF = Files.walk(folderMosinPlusFolderZakaz)
                    .filter(Files::isRegularFile)
                    // .map(Path::toString)
                    .filter(x -> x.toFile().getName().endsWith(".dxf"))
                    .filter(filterPoziciya)                                           //  поз или не поз
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        List<Poziciya> tmpList = filterListPoz.stream()
                .filter(x -> x.getFileName().equals("NOT FILE"))
                .collect(Collectors.toList());

        for (Poziciya poz : tmpList) {
            for (Path path : listPathDXF) {
                if (path.getFileName().toString().startsWith(poz.getName())) {
                    poz.setAbslutFileName(path.toAbsolutePath());
                    poz.setFlagDXF("DXF");
                    poz.setFileName(poz.getName() + ".dxf");
                    break;
                }
            }
            poz.setFileName(poz.getName() + ".dxf");
        }
    }


    private void serchPDF() {
        // 1 - удаляем старые файлы в папке PDF

        for (File file : Objects.requireNonNull(folderPDF.toFile().listFiles()))
            if (!file.isDirectory()) {
                file.delete();
            }

        List<Path> pathPDFfiles = null;                               // mojno li oboites' bez etogo
        folderMosinPlusFolderZakaz = Path.of(mosinTextField.getText());

        Predicate<Path> condition = n -> {
            if (pozCheckBox.isSelected()) {
                return true;
            }
            return !n.toFile().getName().contains("_Поз-");
        };


        try {
            //  теперь нам нужны из этой папки все файлы pdf
            pathPDFfiles = Files.walk(folderMosinPlusFolderZakaz)
                    .filter(Files::isRegularFile)
                    // .map(Path::toString)
                    .filter(x -> x.toFile().getName().endsWith(".pdf"))
                    .filter(condition)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Poziciya> tmpList = filterListPoz.stream()
                .filter(x -> x.getFileName().equals("NOT FILE"))
                .collect(Collectors.toList());

        for (Poziciya poz : tmpList) {
            String str = "-" + poz.getName() + ".pdf";
            for (Path path : pathPDFfiles) {
                if (path.toString().endsWith(str)) {
                    copyPDFtoFolderSwing(path, poz);
                    break;
                }
            }
        }
    }

    private void copyPDFtoFolderSwing(Path path, Poziciya poz) {
        Path pathTarget = folderPDF.resolve(Path.of(poz.getName() + ".pdf"));
        try {
            Files.copy(path, pathTarget, REPLACE_EXISTING);      // , COPY_ATTRIBUTES, NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        poz.setFlagPDF("PDF");
    }


    private void copyFilesInDXFfolder() {
        for (File file : Objects.requireNonNull(folderMyDXF.toFile().listFiles()))
            if (!file.isDirectory()) {
                file.delete();
            }
        filterListPoz.forEach(this::copyPoz);
    }

    private void copyPoz(Poziciya p) {
        if (p.getAbslutFileName() != null) {
            Path pathTarget = folderMyDXF.resolve(p.getFileName());
            try {
                Files.copy(p.getAbslutFileName(), pathTarget, REPLACE_EXISTING); // , COPY_ATTRIBUTES, NOFOLLOW_LINKS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveInFile() {
        Path name = Path.of(thickness.get(getThickness.getSelectedIndex()) + ".txt");
        File saveTextPlusName = saveText.resolve(name).toFile();
        try (PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(saveTextPlusName)))) {
            listPrintPoz.forEach(out::print);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewSearchFiles() {
        textArea1.setText("");
        Collections.sort(filterListPoz);

        long count = filterListPoz.stream()
                .filter(x -> x.getFlagDXF() != null)
                .count();
        long i = filterListPoz.size();
        long k = filterListPoz.stream()
                .filter(poz -> poz.getStatus().equals("---"))
                .count();
        listPrintPoz = new ArrayList<>();

        for (Poziciya poz : filterListPoz) {
            if (filterNOTFILE.test(poz)) {
                textArea1.append(viewPoziciya(poz));
                listPrintPoz.add(viewPoziciya(poz));
            }
        }
        textArea1.append(" ".repeat(4));
        listPrintPoz.add(" ".repeat(4));
        textArea1.append("-".repeat(40));
        listPrintPoz.add("-".repeat(40));
        textArea1.append(System.lineSeparator());
        listPrintPoz.add(System.lineSeparator());
        String str = String.format("   записей= %d , inBaza=  %d , inMosin= %d         НЕ найдено= %d  ", i, i - k, count, k - count);
        textArea1.append(str);
        listPrintPoz.add(str);
    }

    private String viewPoziciya(Poziciya p) {
        StringBuilder sb = new StringBuilder();
        sb.append(addSpace(p.getFileName().replace(".dxf", ""), maxLengthFileName));
        sb.append("\t");
        sb.append(p.getCountT() + p.getCountN());
        sb.append("\t");
        sb.append(addSpace(p.getInv() + "_" + p.getName(), maxLenghtINV + maxLenghtName + 1));
        sb.append("\t");
        sb.append(p.getGeomMashines());
        sb.append("   ");
        sb.append(p.getGabariti());
        sb.append("   ");
        sb.append(p.getFlagPDF());
        sb.append("   ");
        sb.append(p.getFlagDXF());
        sb.append(System.lineSeparator());
        return sb.toString();
    }


    private void ViewCSV() {
        count = 0;
        textArea1.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea1.setText("");

        filterListPoz = list.stream()
                .filter(filterThickness)
                .filter(filterInv)
                .filter(filterMore)
                .filter(filterContains)
                .collect(Collectors.toList());

        filterListPoz.forEach(this::viewPoz);
        textArea1.append(System.lineSeparator());
        textArea1.append("   записей = " + count);
    }

    private void viewPoz(Poziciya p) {
        textArea1.append(addSpace(p.getInv(), maxLenghtINV));
        textArea1.append(addSpace(p.getName(), maxLenghtName));
        textArea1.append("\t");
        textArea1.append(String.valueOf(p.getCountT()));
        textArea1.append("\t");
        textArea1.append(String.valueOf(p.getCountN()));
        textArea1.append("\t");
        textArea1.append(p.getGabariti());
        textArea1.append(System.lineSeparator());
        count++;
    }

    private String addSpace(String str, int maxLenght) {
        if (str.length() == maxLenght) {
            return "   " + str + "     ";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("   ").append(str);
        int razn = maxLenght - str.length();
        for (int i = 0; i < razn + 5; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }


    private void searchFiles(Poziciya poz) {
        Path rootFilePath = pathBazaDXF.resolve(Path.of(poz.getInv()));
        File rootFile = rootFilePath.toFile();

        String litera = "";
        int selectMashines = getMachine.getSelectedIndex();

        if (checkBoxOnlySelectedMashine.isSelected()) {
            litera = mashines[selectMashines];
            // String name = chekNameFromCSV(poz);
            String searchFileName = chekNameFromCSV(poz) + mashines[getMachine.getSelectedIndex()] + ".dxf";
            if (rootFile.isDirectory()) {
                File[] directoryFiles = rootFile.listFiles();
                if (directoryFiles != null) {
                    // ishem  1 sposobom
                    metodSearchFiles(directoryFiles, searchFileName, poz, litera);

                    //esali fail ne nashelsya togda  ishem 2 sposob
                    if (!poz.getStatus().equals("OK")) {
                        searchFileName = "." + poz.getName() + litera + ".dxf";
                        metodSearchFiles(directoryFiles, searchFileName, poz, litera);
                    }
                }
            }
        } else {
            for (int i = selectMashines; i > -1; i--) {
                String searchFileName = chekNameFromCSV(poz) + mashines[selectMashines] + ".dxf";
                if (rootFile.isDirectory()) {
                    File[] directoryFiles = rootFile.listFiles();
                    if (directoryFiles != null) {
                        litera = mashines[selectMashines];
                        // ishem  1 sposobom
                        if (metodSearchFiles(directoryFiles, searchFileName, poz, litera)) {
                            break;
                        }

                        //esali fail ne nashelsya togda  ishem 2 sposob
                        if (!poz.getStatus().equals("OK")) {
                            litera = mashines[selectMashines];
                            if (metodSearchFiles(directoryFiles, "." + poz.getName() + litera + ".dxf", poz, litera)) {
                                break;
                            }
                        }
                    }
                }
                selectMashines--;
            }
        }
    }


    private boolean metodSearchFiles(File[] directoryFiles, String fileSearch, Poziciya poz, String litera) {
        boolean found = false;
        for (File file : directoryFiles) {
            if (file.getName().endsWith(fileSearch)) {
                if (file.getName().contains(poz.getInv())) {
                    Path pathSource = Path.of(file.getAbsolutePath());
                    poz.setStatus("OK");
                    poz.setAbslutFileName(pathSource);
                    String realFileName = changedCharsInFaileName(file.getName());

                    // -4  отбрасываем  расширение .dxf
                    maxLengthFileName = Math.max(maxLengthFileName, realFileName.length() - 4);
                    poz.setFileName(realFileName);
                    String literaWithProbel = litera + " ".repeat(5 - litera.length());
                    poz.setGeomMashines(literaWithProbel);
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    private String chekNameFromCSV(Poziciya poz) {
        boolean firstCharM = poz.getName().charAt(0) == 'М';
        //   boolean sexondCharNumber = Character.isDigit(poz.getName().charAt(1));
        String name = poz.getName();
        if (firstCharM /*&& sexondCharNumber*/) {
            name = name.replace("М", ".m");
        } else {
            name = ".m" + name;
        }
        return name;
    }

    private String changedCharsInFaileName(String s) {
        s = s.substring(s.lastIndexOf('_') + 1);
        s = s.replaceFirst("\\.", "");
        s = s

                .replace("А", "A")
                .replace("Б", "B")
                .replace("В", "V")
                .replace("Г", "G")
                .replace("Д", "D")


                .replace("С", "S")
                .replace("Ф", "F")
                .replace("Л", "L")
                .replace("Г", "G")

                .replace("М", "M")
                .replace("Н", "N")
                .replace("Т", "T")


                .replace("О", "O")
                .replace("П", "P")
                .replace("Р", "R");

        return s;
    }

    public void readCSV(File file) {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("\t\t\t\t\t")
                        || line.equals("\t\t\t\t")
                        || line.equals("Инв.\tОбозначение\tКол.Т\tКол.Н\tГабариты\t")
                        || line.equals("Инв.\tОбозначение\tКол.Т\tКол.Н\tГабариты")) {
                    continue;
                }
                String[] mas = line.split("\t");
                maxLenghtINV = Math.max(maxLenghtINV, mas[0].length());
                maxLenghtName = Math.max(maxLenghtName, mas[1].length());
                list.add(new Poziciya(mas[0], mas[1], Integer.parseInt(mas[2]), Integer.parseInt(mas[3]), mas[4]));
                thicknessSet.add(mas[4].split("x")[0]);
                invSet.add(mas[0]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
