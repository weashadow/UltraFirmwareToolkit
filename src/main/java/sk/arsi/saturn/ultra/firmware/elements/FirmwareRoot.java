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
public class FirmwareRoot {

    private final List<Element> elements = new ArrayList<>();
    private final List<UbiCreateElement> sizeElement = new ArrayList<>();
    private final List<FatloadUsbElement> loadElements = new ArrayList<>();
    private final List<FilePartitionElement> filePartitionElements = new ArrayList<>();

    public List<Element> getElements() {
        return elements;
    }

    public List<UbiCreateElement> getSizeElement() {
        return sizeElement;
    }

    public List<FatloadUsbElement> getLoadElements() {
        return loadElements;
    }

    public List<FilePartitionElement> getFilePartitionElements() {
        return filePartitionElements;
    }


}
