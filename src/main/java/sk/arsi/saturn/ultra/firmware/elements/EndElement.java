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
public class EndElement extends Element {

    public EndElement(FirmwareRoot firmwareRoot, String originalLine) {
        super(firmwareRoot, originalLine);
    }

    @Override
    public String generateOutputLine() {
        return "% <- this is end of script symbol";
    }

}
