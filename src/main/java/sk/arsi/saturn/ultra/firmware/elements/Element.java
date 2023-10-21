/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.arsi.saturn.ultra.firmware.elements;

/**
 *
 * @author arsi
 */
public abstract class Element {

    private final String originalLine;
    private final FirmwareRoot firmwareRoot;

    public Element(FirmwareRoot firmwareRoot, String originalLine) {
        this.originalLine = originalLine;
        this.firmwareRoot = firmwareRoot;
    }

    public String getOriginalLine() {
        return originalLine;
    }

    public FirmwareRoot getFirmwareRoot() {
        return firmwareRoot;
    }

    protected int getLeadingSpacesCount() {
        if (originalLine != null && originalLine.length() > 0) {
            return originalLine.indexOf(originalLine.trim());
        }
        return 0;
    }

    protected String getLeadingSpaces() {
        String tmp = "";
        for (int i = 0; i < getLeadingSpacesCount(); i++) {
            tmp += " ";
        }
        return tmp;
    }

    public String hex(int value) {
        return String.format("0x%01X", value).toLowerCase();
    }

    public String hex(long value) {
        return String.format("0x%01X", value).toLowerCase();
    }

    public String hexUpperCase(int value) {
        return String.format("0x%01X", value);
    }

    public String hexUpperCase(long value) {
        return String.format("0x%01X", value);
    }

    public abstract String generateOutputLine();

}
