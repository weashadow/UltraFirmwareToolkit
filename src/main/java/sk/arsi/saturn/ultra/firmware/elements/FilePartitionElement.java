/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.arsi.saturn.ultra.firmware.elements;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author arsi
 */
public class FilePartitionElement extends Element {

    private final String name;
    private final List<Element> elements = new ArrayList<>();
    private final List<UbiCreateElement> createElements = new ArrayList<>();
    private final List<FatloadUsbElement> loadElements = new ArrayList<>();


    public FilePartitionElement(FirmwareRoot firmwareRoot, String originalLine, String name) {
        super(firmwareRoot, originalLine);
        this.name = name;
        firmwareRoot.getFilePartitionElements().add(this);
    }

    public String getName() {
        return name;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void addElement(Element element) {
        elements.add(element);
        if (element instanceof FatloadUsbElement) {
            loadElements.add((FatloadUsbElement) element);
            if (loadElements.size() > 1) {
                ((FatloadUsbElement) element).setIndex(loadElements.size() - 1);
            }
        } else if (element instanceof UbiCreateElement) {
            createElements.add((UbiCreateElement) element);
        }
    }

    public List<FatloadUsbElement> getFatLoadElements() {
        return loadElements;
    }

    public List<UbiCreateElement> getCreateElements() {
        return createElements;
    }


    @Override
    public String toString() {
        return "Partition" + name; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String generateOutputLine() {
        return getLeadingSpaces() + "# File Partition: " + name;
    }

}
