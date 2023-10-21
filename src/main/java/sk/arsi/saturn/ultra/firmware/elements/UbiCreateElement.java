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
public class UbiCreateElement extends Element {

    private final String volume;
    private final long size;

    public UbiCreateElement(FirmwareRoot firmwareRoot, String originalLine, String volume, long size) {
        super(firmwareRoot, originalLine);
        this.volume = volume;
        this.size = size;
        firmwareRoot.getSizeElements().put(volume + ".es", this);
    }

    public String getVolume() {
        return volume;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Volume: " + volume + " Size: " + size; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String generateOutputLine() {
        //// ubi create rootfs 0x2C00000
        return getOriginalLine();
    }

}
