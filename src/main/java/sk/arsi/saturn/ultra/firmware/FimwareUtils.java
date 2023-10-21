/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.arsi.saturn.ultra.firmware;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import sk.arsi.saturn.ultra.firmware.elements.CrcCheckElement;
import sk.arsi.saturn.ultra.firmware.elements.Element;
import sk.arsi.saturn.ultra.firmware.elements.EmptyElement;
import sk.arsi.saturn.ultra.firmware.elements.EndElement;
import sk.arsi.saturn.ultra.firmware.elements.FatloadUsbElement;
import sk.arsi.saturn.ultra.firmware.elements.FilePartitionElement;
import sk.arsi.saturn.ultra.firmware.elements.FirmwareRoot;
import sk.arsi.saturn.ultra.firmware.elements.MtdPartsElement;
import sk.arsi.saturn.ultra.firmware.elements.NandEraseElement;
import sk.arsi.saturn.ultra.firmware.elements.NandWriteElement;
import sk.arsi.saturn.ultra.firmware.elements.SaveEnvElement;
import sk.arsi.saturn.ultra.firmware.elements.SetEnvElement;
import sk.arsi.saturn.ultra.firmware.elements.StartElement;
import sk.arsi.saturn.ultra.firmware.elements.UbiCreateElement;
import sk.arsi.saturn.ultra.firmware.elements.UbiPartElement;
import sk.arsi.saturn.ultra.firmware.elements.UbiWriteElement;
import sk.arsi.saturn.ultra.firmware.elements.UpgradeBinElement;
import sk.arsi.saturn.ultra.firmware.elements.UpgradeForceElement;
import sk.arsi.saturn.ultra.firmware.elements.WriteCisElement;

/**
 *
 * @author arsi
 */
public class FimwareUtils {

    public static FirmwareRoot parseElements(File firmwareImage) throws FileNotFoundException, IOException {
        System.out.println("Parsing firmware configuration...");
        FirmwareRoot firmwareRoot = new FirmwareRoot();
        List<Element> elements = firmwareRoot.getElements();
        BufferedReader reader = new BufferedReader(new FileReader(firmwareImage));
        String originalLine = reader.readLine();
        String line = originalLine.trim();
        FilePartitionElement partitionElement = null;
        boolean end = false;
        while (line != null && end == false) {
            if (line.startsWith("# <- this is for comment")) {
                elements.add(new StartElement(firmwareRoot, originalLine));
            } else if (line.startsWith("#upgrade_bin_version")) {
                elements.add(new UpgradeBinElement(firmwareRoot, originalLine, line.split("=")[1]));
            } else if (line.startsWith("#upgrade_force")) {
                elements.add(new UpgradeForceElement(firmwareRoot, originalLine, Integer.valueOf(line.split("=")[1])));
            } else if (line.startsWith("# File Partition:")) {
                partitionElement = new FilePartitionElement(firmwareRoot, originalLine, line.replace("# File Partition:", "").trim());
                elements.add(partitionElement);
            } else if (line.startsWith("fatload usb") && partitionElement != null) {
                String[] split = line.split(" ");
                //fatload usb 0 0x21000000 $(UpgradeImage) 0x5608 0x4000
                final int deviceNumber = Integer.valueOf(split[2]);
                final long loadAddress = Long.valueOf(split[3].replace("0x", ""), 16);
                final String filename = split[4];
                int length = Integer.valueOf(split[5].replace("0x", ""), 16);
                int pos = Integer.valueOf(split[6].replace("0x", ""), 16);
                partitionElement.addElement(new FatloadUsbElement(firmwareRoot, partitionElement, originalLine, deviceNumber, loadAddress, filename, length, pos));
            } else if (line.startsWith("crccheck") && partitionElement != null) {
                //crccheck 0x21000000 0x5608
                String[] split = line.split(" ");
                final long loadAddress = Long.valueOf(split[1].replace("0x", ""), 16);
                final long length = Long.valueOf(split[2].replace("0x", ""), 16);
                partitionElement.addElement(new CrcCheckElement(firmwareRoot, partitionElement, originalLine, loadAddress, length));
            } else if (line.startsWith("writecis") && partitionElement != null) {
                //writecis 0x21000000 0x21800000 10 0 0 5
                partitionElement.addElement(new WriteCisElement(firmwareRoot, originalLine, line));
            } else if (line.startsWith("mtdparts del") && partitionElement != null) {
                //mtdparts del CIS
                partitionElement.addElement(new MtdPartsElement(firmwareRoot, originalLine, line));
            } else if (line.startsWith("setenv") && partitionElement != null) {
                partitionElement.addElement(new SetEnvElement(firmwareRoot, originalLine, line));
            } else if (line.startsWith("saveenv") && partitionElement != null) {
                partitionElement.addElement(new SaveEnvElement(firmwareRoot, originalLine));
            } else if (line.startsWith("nand erase.part") && partitionElement != null) {
                //nand erase.part UBI
                partitionElement.addElement(new NandEraseElement(firmwareRoot, originalLine, line));
            } else if (line.startsWith("ubi part UBI") && partitionElement != null) {
                //ubi part UBI
                partitionElement.addElement(new UbiPartElement(firmwareRoot, originalLine, line));
            } else if (line.startsWith("ubi write") && partitionElement != null) {
                //ubi write 0x21000000 rootfs 0x1F7C000
                String[] split = line.split(" ");
                final long address = Long.valueOf(split[2].replace("0x", ""), 16);
                final String volume = split[3];
                final int size = Integer.valueOf(split[4].replace("0x", ""), 16);
                partitionElement.addElement(new UbiWriteElement(firmwareRoot, partitionElement, originalLine, address, volume, size));
            } else if (line.startsWith("ubi create") && partitionElement != null) {
                // ubi create rootfs 0x2C00000
                String[] split = line.split(" ");
                final String volume = split[2];
                final long size = Long.valueOf(split[3].replace("0x", ""), 16);
                partitionElement.addElement(new UbiCreateElement(firmwareRoot, originalLine, volume, size));
            } else if (line.startsWith("nand write.e")) {
                //nand write.e 0x21000000 IPL0 0x5BE0
                String[] split = line.split(" ");
                final long address = Long.valueOf(split[2].replace("0x", ""), 16);
                final String partName = split[3];
                long length = Long.valueOf(split[4].replace("0x", ""), 16);
                partitionElement.addElement(new NandWriteElement(firmwareRoot, originalLine, address, partName, length));
            } else if (line.startsWith("%")) {
                partitionElement.addElement(new EndElement(firmwareRoot, originalLine));
                end = true;
            } else if (line.isEmpty()) {
                elements.add(new EmptyElement(firmwareRoot, originalLine));
            } else {
                System.out.println("Element not decoded: " + line);
            }
            originalLine = reader.readLine();
            line = originalLine.trim();
        }

        return firmwareRoot;
    }

}
