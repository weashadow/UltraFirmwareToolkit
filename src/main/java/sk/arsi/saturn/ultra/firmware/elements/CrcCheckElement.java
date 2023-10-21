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
public class CrcCheckElement extends Element {

    private final long loadAddress;
    private long length;

    public CrcCheckElement(FirmwareRoot firmwareRoot, String originalLine, long loadAddress, long length) {
        super(firmwareRoot, originalLine);
        this.loadAddress = loadAddress;
        this.length = length;
    }

    public long getLoadAddress() {
        return loadAddress;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }


}
