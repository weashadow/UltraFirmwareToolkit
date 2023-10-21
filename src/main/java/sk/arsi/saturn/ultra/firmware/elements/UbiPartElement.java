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
public class UbiPartElement extends Element {

    private final String volume;

    public UbiPartElement(FirmwareRoot firmwareRoot, String originalLine, String volume) {
        super(firmwareRoot, originalLine);
        this.volume = volume;
    }

    public String getVolume() {
        return volume;
    }

    @Override
    public String generateOutputLine() {
        //ubi part UBI
        return getOriginalLine();
    }

}
