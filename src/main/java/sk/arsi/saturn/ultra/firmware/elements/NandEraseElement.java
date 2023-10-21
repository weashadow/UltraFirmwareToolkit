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
public class NandEraseElement extends Element {

    private final String partName;

    public NandEraseElement(FirmwareRoot firmwareRoot, String originalLine, String partName) {
        super(firmwareRoot, originalLine);
        this.partName = partName;
    }

    public String getPartName() {
        return partName;
    }

    @Override
    public String generateOutputLine() {
        //nand erase.part UBI
        return getLeadingSpaces() + "nand erase.part " + partName;
    }

}
