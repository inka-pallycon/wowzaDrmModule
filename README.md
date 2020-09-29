# PallyCon Wowza CPIX Module

## Overview
PallyCon Wowza Integration SDK is an extension module of Wowza Streaming Engine that supports streaming service with DASH (CENC) and HLS (FPS) content by packaging original MP4 video or live stream in real time.

### Streaming protocol and DRM support

| HLS FairPlay | Dash Widevine | Dash PlayReady |
|:------|:------|:------|
| :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |



DASH-CENC : Widevine Modular, PlayReady DRM  
HLS-AES : FairPlay Streaming DRM

allyCon Wowza Integration SDK supports Wowza Streaming Engine 4.8 or later or later and recommends interworking of storage and CDN service through Wowza Media Cache to improve streaming performance.

For more information on the Wowza Streaming Engine and a demonstration of the PallyCon Wowza Interaction SDK, please see the links below.

- Wowza Streaming Engine website : https://www.wowza.com/products/streaming-engine
- Wowza real time packaging demo : https://www.pallycon.com/multi-drm-demo/

## Setting Wowza Integration Module
### Required
- JAVA version : jdk 9 +
### Creating Wowza Application
Install the Wowza Streaming Engine on a server for live streaming. Create the Wowza streaming application on the Applications tab after connecting to http://Wowza-server:8088/ on your browser. You can choose VOD or live applications. If you use both methods, you have to create and configure each application separately.

Note: If you are applying both Multi DRM (PlayReady, Widevine, FPS), you must create and configure each Wowza application separately.

Setting Libraries
Copy the files in the /jar folder of the unpacked PallyCon Wowza Interaction SDK to the [WOWZA_HOME]/lib/ folder.

- PallyCon Wowza Integration SDK file can be requested from PallyCon Console site when applying for commercial service and can be downloaded from the service information page after the request is approved.

```
- pallycon-cpix-common-2.0.0-jar-with-dependencies.jar
- pallycon-wowza-cpix-1.0.0.jar
- protobuf-java-2.6.1.jar
```


### Configuring Wowza Module
Add PallyCon module related setting in /[WOWZA_HOME]/conf/[APPLICATION]/Application.xml. Wowza application for Multi DRM (PlayReady, Widevine, FPS) and NCG DRM application should be separately created and set up as below.

#### Add property for FairPlay
Configure Wowza settings as below by referring to the Wowza guide. (https://www.wowza.com/docs/how-to-configure-apple-hls-packetization-cupertinostreaming)

```xml
<!-- add property -->
<LiveStreamPacketizer>
    <Properties>
        <Property>
               <Name>cupertinoChunkDurationTarget</Name>
                <Value>10000</Value>
                <Type>Integer</Type>
        </Property>
        <Property>
                <Name>cupertinoMaxChunkCount</Name>
                <Value>10</Value>
                <Type>Integer</Type>
        </Property>
        <Property>
                <Name>cupertinoPlaylistChunkCount</Name>
                <Value>3</Value>
                <Type>Integer</Type>
        </Property>
        <Property>
                <Name>cupertinoRepeaterChunkCount</Name>
                <Value>-1</Value>
                <Type>Integer</Type>
        </Property>
        <Property>
                <Name>cupertinoCalculateChunkIDBasedOnTimecode</Name>
                <Value>false</Value>
                <Type>Boolean</Type>
        </Property>
	</Properties>
</LiveStreamPacketizer>
```
​
#### Add HTTPStreamer property for FairPlay
 ```xml
 <!-- add property -->
 <HTTPStreamer>
    <Properties>
        <Property>
            <Name>cupertinoExtXVersion</Name>
            <Value>5</Value>
            <Type>Integer</Type>
        </Property>
    </Properties>
</HTTPStreamer>
 ```

#### Configure Session ID Option for FairPlay
By default, Wowza Streaming Engine adds a streaming session ID to the encryption URI value in the HLS chunklist as shown below.

```
e.g. sdk://content-id?wowzasessionid=30273096
```
For PallyCon FairPlay integration, you need to change the Wowza setting so that the session ID is not added to that value. Using the following guide, set the `cupertinoAppendQueryParamsToEncUrl` property to `false`.

- https://www.wowza.com/docs/how-to-control-streaming-session-id-appended-to-encryption-urls-in-chunklist-responses-cupertinoappendqueryparamstoencurl



### Add module for Multi-DRM
```xml
<Modules>
    ...
    <Module>
        <!-- add property -->
        <!-- Settings for MPEG-DASH(Widevine, PlayReady), HLS(FairPlayStream) -->
        <Name>DrmModule</Name>
            <Descript>Multi DRM CPIX Module</Descript>
            <Class>com.pallycon.wowza.DrmModule</Class>
        <Description></Description>
	</Module>
</Modules>
```

###Add property for Multi-DRM
```xml
<Properties>
    ...
    <!-- add property -->
    <Property>
        <Name>cupertinoEncryptionAPIBased</Name>
        <Value>true</Value>
        <Type>Boolean</Type>
    </Property>
    <Property>
        <Name>KmsUrl</Name>
        <!-- pallycon kms v1 url-->
        <Value>https://kms.pallycon.com/cpix/pallycon/getKey</Value>
    </Property>
     <Property>
     <!-- true : live key rotation / false : single key -->
        <Name>KeyRotation</Name>
        <Value>false</Value>
    </Property>
    <Property>
        <Name>PallyConEncToken</Name>
        <Value>{pallycon kms token}</Value>
    </Property>
</Properties>
```
Collapse
