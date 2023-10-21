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
public class FatloadUsbElement extends Element {

    private final int deviceNumber;
    private int index = 0;
    private final long loadAddress;
    private final String filename;
    private long length;
    private long position;
    private FilePartitionElement partitionElement;

    public FatloadUsbElement(FirmwareRoot firmwareRoot, FilePartitionElement partitionElement, String originalLine, int deviceNumber, long loadAddress, String filename, long length, long position) {
        super(firmwareRoot, originalLine);
        this.deviceNumber = deviceNumber;
        this.loadAddress = loadAddress;
        this.filename = filename;
        this.length = length;
        this.position = position;
        this.partitionElement = partitionElement;
        firmwareRoot.getLoadElements().add(this);
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public long getLoadAddress() {
        return loadAddress;
    }

    public String getFilename() {
        return filename;
    }

    public long getLength() {
        return length;
    }

    public long getPosition() {
        return position;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getPartitionName() {
        String n = partitionElement.getName();
        if (index > 0) {
            return n + "." + index;
        }
        return n;
    }

    @Override
    public String toString() {
        return "Name: " + getPartitionName() + " Position: " + position + " Length: " + length; //To change body of generated methods, choose Tools | Templates.
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
