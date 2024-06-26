

# UltraFirmwareToolkit

Saturn 3 Ultra firmware mod toolkit

Supported printers:
 - Saturn 3 Ultra
 - Uniformation GKTwo

*S3U and GKTwo Firmware mod is tested and fully functional* 

<b> Modified firmware for Saturn 3 ultra and Uniformation GKTwo can be found [here](
https://github.com/weashadow/UltraFirmwareToolkit/releases).</b>



## Use of this software is at your own risk, the author is not responsible for damages caused by modification of the original firmware!


**Dependencies:**<br/>
[UBI Reader](https://github.com/onekey-sec/ubi_reader)<br/>
*If you install UbiReader via pip, you need to install it via sudo as root..*
> sudo pip install --user ubi_reader

[Binwalk](https://github.com/ReFirmLabs/binwalk)<br/>
> sudo apt install binwalk

[Java](https://www.azul.com/downloads/?package=jdk#zulu)

**Usage:**<br/>
Copy the ChituUpgrade.bin file to the scripts directory and run <b>extract.sh</b><br/>
<b>extract.sh</b> generates disk images in UBI format (*.es) and the checksum files (*.es.crc) and scripts <b>extract_partition.sh, build.sh</b><br/>
Use <b>extract_partition.sh</b> to extract the contents of disk images files to the directories<br/>


After editing the filesystem, use <b>build.sh</b> to create new UBI disk images and crc files (*.out.es) and (*.out.es.crc) and new image ChituUpgrade.bin.out<br/>
<br/>

Clear all generated files and directories with clear.sh<br/>


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
