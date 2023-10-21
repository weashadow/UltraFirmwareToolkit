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

    private final String version;

    public UpgradeBinElement(FirmwareRoot firmwareRoot, String originalLine, String version) {
        super(firmwareRoot, originalLine);
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String generateOutputLine() {
        return getOriginalLine();
    }

}
