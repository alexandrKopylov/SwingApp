import java.nio.file.Path;

public class Poziciya implements Comparable<Poziciya> {
    private String inv;
    private String name;
    private int countT;
    private int countN;
    private String gabariti;
    private String status = "---";
    private String geomMashines;
    private String fileName = "NOT FILE";
    private Path abslutFileName;
    private boolean flagSearchDXF;
    private String flagPDF;
    private String flagDXF;

    public Poziciya(String inv, String name, int countT, int countN, String gabariti) {
        this.inv = inv;
        this.name = name;
        this.countT = countT;
        this.countN = countN;
        this.gabariti = gabariti;
    }

    public String getFlagPDF() {
        return flagPDF;
    }

    public void setFlagPDF(String flagPDF) {
        this.flagPDF = flagPDF;
    }

    public String getFlagDXF() {
        return flagDXF;
    }

    public void setFlagDXF(String flagDXF) {
        this.flagDXF = flagDXF;
    }

    public boolean isFlagSearchDXF() {
        return flagSearchDXF;
    }

    public void setFlagSearchDXF(boolean flagSearchDXF) {
        this.flagSearchDXF = flagSearchDXF;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getInv() {
        return inv;
    }

    public String getName() {
        return name;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCountT() {
        return countT;
    }

    public int getCountN() {
        return countN;
    }

    public String getGabariti() {
        return gabariti;
    }

    public void setGabariti(String gabariti) {
        this.gabariti = gabariti;
    }

    public Path getAbslutFileName() {
        return abslutFileName;
    }

    public void setAbslutFileName(Path abslutFileName) {
        this.abslutFileName = abslutFileName;
    }

    public String getGeomMashines() {
        return geomMashines;
    }

    public void setGeomMashines(String geomMashines) {
        this.geomMashines = geomMashines;
    }

    @Override
    public String toString() {
        return inv + "  " + name + "  " + flagSearchDXF + "   "  + System.lineSeparator();
    }

    @Override
    public int compareTo(Poziciya o) {
        return fileName.compareTo(o.fileName);
    }
}