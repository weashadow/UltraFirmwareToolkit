/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.arsi.saturn.ultra.firmware.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author arsi
 */
public class FilePartitionElement extends Element {

    private final String name;
    private final List<Element> elements = new ArrayList<>();
    private final List<UbiCreateElement> createElements = new ArrayList<>();
    private final List<FatloadUsbElement> loadElements = new ArrayList<>();
    private final Map<String, CrcCheckElement> crcElements = new HashMap<>();
    private final Map<String, UbiWriteElement> ubiWriteElements = new HashMap<>();


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
        getFirmwareRoot().getElements().add(element);
        elements.add(element);
        if (element instanceof FatloadUsbElement) {
            loadElements.add((FatloadUsbElement) element);
            if (loadElements.size() > 1) {
                ((FatloadUsbElement) element).setIndex(loadElements.size() - 1);
            }
        } else if (element instanceof UbiCreateElement) {
            createElements.add((UbiCreateElement) element);
        } else if (element instanceof CrcCheckElement) {
            if (loadElements.size() > 0) {
                ((CrcCheckElement) element).setIndex(loadElements.size() - 1);
                crcElements.put(((CrcCheckElement) element).getPartitionName(), ((CrcCheckElement) element));
            }

        } else if (element instanceof UbiWriteElement) {
            if (loadElements.size() > 0) {
                ((UbiWriteElement) element).setIndex(loadElements.size() - 1);
                ubiWriteElements.put(((UbiWriteElement) element).getPartitionName(), ((UbiWriteElement) element));
            }

        }
    }

    public List<FatloadUsbElement> getFatLoadElements() {
        return loadElements;
    }

    public List<UbiCreateElement> getCreateElements() {
        return createElements;
    }

    public CrcCheckElement getCrcCheckElement(String partitionName) {
        return crcElements.get(partitionName);
    }

    public UbiWriteElement getUbiWriteElement(String partitionName) {
        return ubiWriteElements.get(partitionName);
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
