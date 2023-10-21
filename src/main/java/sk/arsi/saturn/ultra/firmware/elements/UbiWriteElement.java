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
public class UbiWriteElement extends Element {

    private final long address;
    private final String volume;
    private final long size;

    public UbiWriteElement(FirmwareRoot firmwareRoot, String originalLine, long address, String volume, long size) {
        super(firmwareRoot, originalLine);
        this.address = address;
        this.volume = volume;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public long getAddress() {
        return address;
    }

    public String getVolume() {
        return volume;
    }

    @Override
    public String generateOutputLine() {
        //ubi write 0x21000000 rootfs 0x1F7C000
        return getLeadingSpaces() + "ubi write " + hex(address) + " " + volume + " " + hex(size);
    }

}
