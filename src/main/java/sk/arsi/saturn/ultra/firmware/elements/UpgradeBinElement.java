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
public class UpgradeBinElement extends Element {

    private String version;
    private final String originalVersion;

    public UpgradeBinElement(FirmwareRoot firmwareRoot, String originalLine, String version) {
        super(firmwareRoot, originalLine);
        this.version = version;
        this.originalVersion = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    @Override
    public String generateOutputLine() {
        return getOriginalLine().replace(originalVersion, version);
    }

}
