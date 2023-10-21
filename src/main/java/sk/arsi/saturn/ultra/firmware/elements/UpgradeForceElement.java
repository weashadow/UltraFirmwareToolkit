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
public class UpgradeForceElement extends Element {

    private final int force;

    public UpgradeForceElement(FirmwareRoot firmwareRoot, String originalLine, int force) {
        super(firmwareRoot, originalLine);
        this.force = force;
    }

    public int getForce() {
        return force;
    }

    @Override
    public String generateOutputLine() {
        return getOriginalLine();
    }

}
