

# UltraFirmwareToolkit

Saturn 3 Ultra firmware mod toolkit

*Work in progress... All that's left is to produce new firmware and upload it to the printer.* 


## Use of this software is at your own risk, the author is not responsible for damages caused by modification of the original firmware!


**Dependencies:**<br/>
[UBI Reader](https://github.com/onekey-sec/ubi_reader)<br/>
*If you install UbiReader via pip, you need to install it via sudo as root..*
> sudo pip install --user ubi_reader

[Binwalk](https://github.com/ReFirmLabs/binwalk)<br/>
> sudo apt install binwalk

[Java](https://www.azul.com/downloads/?package=jdk#zulu)

**Usage:**<br/>
Copy the ChituUpgrade.bin file to the scripts directory and run extract.sh<br/>

From the firmware file, the disk images in UBI format are extracted (*.es) and the checksum files (*.es.crc) <br/>
And finally the directories in which the file system is located are extracted... <br/>

After editing the filesystem, use build.sh to create new UBI disk images and crc files..<br/>
(*.out.es) and (*.out.es.crc)<br/>

Clear all generated files and directories with clear.sh<br/>

**ToDo:**<br/>
Generating new firmware file from UBI disk images and crc<br/>

**Partitions:**<br/>
> rootfs 44 MB<br/>
> miservice 10 MB<br/>
> customer 40 MB<br/>
> appconfigs 4 MB<br/>
> parameter 4 MB<br/>

**Format of executable files:**<br/>
ARM. EABI5 version1, hard float<br/>
You can use packages armv7h from [ARCH ARM](https://archlinuxarm.org/packages)<br/>
But you also have to add all the package dependencies.<br/>
To crosscompile your source codes follow the same procedure as for hard float RPI<br/>
Printer services are started via /etc/init.d/rcS<br/>
