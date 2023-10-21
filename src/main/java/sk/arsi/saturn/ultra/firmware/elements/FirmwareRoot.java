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
public class FirmwareRoot {

    private final List<Element> elements = new ArrayList<>();
    private final Map<String, UbiCreateElement> sizeElements = new HashMap<>();
    private final List<FatloadUsbElement> loadElements = new ArrayList<>();
    private final List<FilePartitionElement> filePartitionElements = new ArrayList<>();

    public List<Element> getElements() {
        return elements;
    }

    public UbiCreateElement getSizeElement(String partitionName) {
        return sizeElements.get(partitionName);
    }

    public Map<String, UbiCreateElement> getSizeElements() {
        return sizeElements;
    }


    public List<FatloadUsbElement> getLoadElements() {
        return loadElements;
    }

    public List<FilePartitionElement> getFilePartitionElements() {
        return filePartitionElements;
    }


}
