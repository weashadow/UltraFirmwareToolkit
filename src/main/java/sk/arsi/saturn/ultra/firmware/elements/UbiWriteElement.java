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

    protected final long address;
    protected final String volume;
    protected int size;
    protected FilePartitionElement partitionElement;
    protected int index = 0;

    public UbiWriteElement(FirmwareRoot firmwareRoot, FilePartitionElement partitionElement, String originalLine, long address, String volume, int size) {
        super(firmwareRoot, originalLine);
        this.address = address;
        this.volume = volume;
        this.size = size;
        this.partitionElement = partitionElement;
    }

    public String getPartitionName() {
        String n = partitionElement.getName();
        if (index > 0) {
            return n + "." + index;
        }
        return n;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
        return getLeadingSpaces() + "ubi write " + hexUpperCase(address) + " " + volume + " " + hexUpperCase(size);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
