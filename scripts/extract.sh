java -jar UltraFirmwareToolkit.jar -extract ChituUpgrade.bin 
sudo ubireader_extract_files -k rootfs.es -o rootfs
sudo ubireader_extract_files -k miservice.es -o miservice
sudo ubireader_extract_files -k customer.es -o customer
sudo ubireader_extract_files -k appconfigs.es -o appconfigs
sudo ubireader_extract_files -k parameter.es -o parameter
