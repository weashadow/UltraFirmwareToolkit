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
    private FilePartitionElement partitionElement;
    private int index = 0;

    public CrcCheckElement(FirmwareRoot firmwareRoot, FilePartitionElement partitionElement, String originalLine, long loadAddress, long length) {
        super(firmwareRoot, originalLine);
        this.loadAddress = loadAddress;
        this.length = length;
        this.partitionElement = partitionElement;
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

    public String getPartitionName() {
        String n = partitionElement.getName();
        if (index > 0) {
            return n + "." + index;
        }
        return n;
    }

    @Override
    public String generateOutputLine() {
        //crccheck 0x21000000 0x5608
        return getLeadingSpaces() + "crccheck " + hex(loadAddress) + " " + hex(length);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
