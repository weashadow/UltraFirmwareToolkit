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
public class NandWriteElement extends Element {

    private final long address;
    private final String partName;
    private long length;

    public NandWriteElement(FirmwareRoot firmwareRoot, String originalLine, long address, String partName, long length) {
        super(firmwareRoot, originalLine);
        this.address = address;
        this.partName = partName;
        this.length = length;
    }

    public long getAddress() {
        return address;
    }

    public String getPartName() {
        return partName;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    @Override
    public String generateOutputLine() {
        //nand write.e 0x21000000 IPL0 0x5BE0
        return getLeadingSpaces() + "nand write.e " + hex(address) + " " + partName + " " + hex(length);
    }

}
