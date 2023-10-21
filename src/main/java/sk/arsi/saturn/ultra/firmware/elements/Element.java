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


}
