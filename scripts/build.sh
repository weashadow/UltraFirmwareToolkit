sudo mkfs.ubifs -m 2048 -e 126976 -c 353 -x lzo -o rootfs.out.es -r rootfs --space-fixup
crc32 rootfs.out.es | tr -d "\n" > rootfs.out.es.crc
sudo chmod 666 rootfs.out.es
sudo mkfs.ubifs -m 2048 -e 126976 -c 84 -x lzo -o miservice.out.es -r miservice --space-fixup
crc32 miservice.out.es | tr -d "\n" > miservice.out.es.crc
sudo chmod 666 miservice.out.es
sudo mkfs.ubifs -m 2048 -e 126976 -c 321 -x lzo -o customer.out.es -r customer --space-fixup
crc32 customer.out.es | tr -d "\n" > customer.out.es.crc
sudo chmod 666 customer.out.es
sudo mkfs.ubifs -m 2048 -e 126976 -c 36 -x lzo -o appconfigs.out.es -r appconfigs --space-fixup
crc32 appconfigs.out.es | tr -d "\n" > appconfigs.out.es.crc
sudo chmod 666 appconfigs.out.es
sudo mkfs.ubifs -m 2048 -e 126976 -c 36 -x lzo -o parameter.out.es -r parameter --space-fixup
crc32 parameter.out.es | tr -d "\n" > parameter.out.es.crc
sudo chmod 666 parameter.out.es
java -jar UltraFirmwareToolkit.jar -build ChituUpgrade.bin 