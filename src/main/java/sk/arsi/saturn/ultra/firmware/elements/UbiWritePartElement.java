/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.arsi.saturn.ultra.firmware.elements;

import java.io.File;

/**
 *
 * @author arsi
 */
public class UbiWritePartElement extends UbiWriteElement {

    private int fullSize = 0;

    public UbiWritePartElement(FirmwareRoot firmwareRoot, FilePartitionElement partitionElement, String originalLine, long address, String volume, int size, int fullSize) {
        super(firmwareRoot, partitionElement, originalLine, address, volume, size);
        this.fullSize = fullSize;
    }

    @Override
    public String generateOutputLine() {
        //ubi write.part 0x21000000 customer 0x2300000 0x250E000
        //ubi write.part 0x21000000 customer 0x2300000
        if (fullSize == 0) {
            return getLeadingSpaces() + "ubi write.part " + hexUpperCase(address) + " " + volume + " " + hexUpperCase(size);
        } else {
            File tmp = new File("./" + volume + ".merged.out.es");
            long length = tmp.length();
            return getLeadingSpaces() + "ubi write.part " + hexUpperCase(address) + " " + volume + " " + hexUpperCase(size) + " " + hexUpperCase(length);
        }
    }

    public int getFullSize() {
        return fullSize;
    }

    public void setFullSize(int fullSize) {
        this.fullSize = fullSize;
    }

    @Override
    public void setSize(int size) {
    }


}
