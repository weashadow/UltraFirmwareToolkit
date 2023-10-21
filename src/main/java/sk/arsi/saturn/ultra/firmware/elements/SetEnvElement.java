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
public class SetEnvElement extends Element {

    private final String data;

    public SetEnvElement(FirmwareRoot firmwareRoot, String originalLine, String data) {
        super(firmwareRoot, originalLine);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public String generateOutputLine() {
        return getOriginalLine();
    }

}
