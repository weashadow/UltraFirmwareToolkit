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
    private int length;
    private int position;
    private FilePartitionElement partitionElement;

    public FatloadUsbElement(FirmwareRoot firmwareRoot, FilePartitionElement partitionElement, String originalLine, int deviceNumber, long loadAddress, String filename, int length, int position) {
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

    public int getLength() {
        return length;
    }

    public int getPosition() {
        return position;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPartitionName() {
        String n = partitionElement.getName();
        if (index > 0) {
            return n + "." + index;
        }
        return n;
    }

    public String getSimplePartitionName() {
        String n = partitionElement.getName();
        return n.substring(0, n.indexOf('.'));
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

    @Override
    public String generateOutputLine() {
        //fatload usb 0 0x21000000 $(UpgradeImage) 0x1d10008 0x2a53000
        return getLeadingSpaces() + "fatload usb " + deviceNumber + " " + hex(loadAddress) + " " + filename + " " + hex(length) + " " + hex(position);
    }

    public FilePartitionElement getPartitionElement() {
        return partitionElement;
    }

}
