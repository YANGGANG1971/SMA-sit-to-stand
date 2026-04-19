clear all;
% Read the hex string from the txt file
filename = 'demo_data.txt';
fileID = fopen(filename, 'r');
% Read the first line of the file
hex_str = fgetl(fileID);
fclose(fileID);

% Remove any potential leading/trailing whitespaces or newline characters
hex_str = strtrim(hex_str);

% Convert the hex string into a uint8 byte array
byte_array = uint8(sscanf(hex_str, '%2x')'); 

% Check if the data frame length is correct (expected 52 bytes)
if length(byte_array) ~= 52
    error('Data frame length mismatch! Expected 52 bytes, got %d bytes.', length(byte_array));
end

% 2. Unpack the byte array into the 'demodata' struct sequentially
demodata = struct(); 
idx = 1;             

% Helper function: convert 4 bytes to int32
bytes_to_int32 = @(b) typecast(b, 'int32');

% -- Frame Header --
demodata.header = dec2hex(byte_array(idx : idx+1)); % uint8_t [2]
idx = idx + 2;

% -- Payload Data (data0 ~ data13) --
demodata.key  = byte_array(idx);                  
idx = idx + 1;

demodata.mode  = byte_array(idx);                  
idx = idx + 1;

demodata.data1  = bytes_to_int32(byte_array(idx : idx+3)); 
idx = idx + 4;

demodata.data2  = bytes_to_int32(byte_array(idx : idx+3)); 
idx = idx + 4;

demodata.data3  = bytes_to_int32(byte_array(idx : idx+3)); 
idx = idx + 4;

demodata.data4  = bytes_to_int32(byte_array(idx : idx+3)); 
idx = idx + 4;

demodata.data5  = bytes_to_int32(byte_array(idx : idx+3)); 
idx = idx + 4;

demodata.data6  = bytes_to_int32(byte_array(idx : idx+3)); 
idx = idx + 4;

demodata.data7  = bytes_to_int32(byte_array(idx : idx+3)); 
idx = idx + 4;

demodata.data8  = bytes_to_int32(byte_array(idx : idx+3)); 
idx = idx + 4;

demodata.data9 = bytes_to_int32(byte_array(idx : idx+3)); 
idx = idx + 4;

demodata.data10 = bytes_to_int32(byte_array(idx : idx+3)); 
idx = idx + 4;

demodata.data11 = bytes_to_int32(byte_array(idx : idx+3)); 
idx = idx + 4;

demodata.status = typecast(byte_array(idx : idx+1), 'uint16'); 
idx = idx + 2;

% -- Frame Tail --
demodata.tail   = dec2hex(byte_array(idx : idx+1)); 

% 3. Display the parsed result
disp('Data unpacking completed. The parsed result is:');
disp(demodata);