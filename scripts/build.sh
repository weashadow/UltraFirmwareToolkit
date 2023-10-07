mkfs.ubifs -m 2048 -e 126976 -c 353 -x lzo -o rootfs.out.es -r rootfs
crc32 rootfs.out.es | tr -d "\n" > rootfs.out.es.crc
mkfs.ubifs -m 2048 -e 126976 -c 353 -x lzo -o miservice.out.es -r miservice
crc32 miservice.out.es | tr -d "\n" > miservice.out.es.crc
mkfs.ubifs -m 2048 -e 126976 -c 353 -x lzo -o customer.out.es -r customer
crc32 customer.out.es | tr -d "\n" > customer.out.es.crc
mkfs.ubifs -m 2048 -e 126976 -c 353 -x lzo -o appconfigs.out.es -r appconfigs
crc32 appconfigs.out.es | tr -d "\n" > appconfigs.out.es.crc
mkfs.ubifs -m 2048 -e 126976 -c 353 -x lzo -o parameter.out.es -r parameter
crc32 parameter.out.es | tr -d "\n" > parameter.out.es.crc
