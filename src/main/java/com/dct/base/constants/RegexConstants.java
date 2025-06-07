package com.dct.base.constants;

/**
 * Some patterns to validate data formats
 * @author thoaidc
 */
@SuppressWarnings("unused")
public interface RegexConstants {

    String USERNAME_PATTERN = "^[a-zA-Z0-9]{2,45}$"; // Includes only numbers and letters

    // The length is between 8 and 20 characters
    // It contains only letters (lowercase/uppercase), numbers, and special characters from a specified list
    String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]{8,20}$";

    // The email address cannot have invalid characters, such as a dot at the beginning or end of the domain name
    String EMAIL_PATTERN = "^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

    // The phone number can have a plus sign (+) at the beginning (for international numbers)
    // The country code can consist of 1 to 3 digits
    // The area code must follow specific rules (e.g., starting with 3, 5, 7, 8, or 9 and followed by certain digits)
    // Spaces, dots, or hyphens can be used to separate parts of the phone number
    // After the area code, there may be one or two groups of three or four digits
    // Examples of valid phone numbers:
    // 0355034078
    // +123 456 7890
    // +84-90-123-4567
    // 091-123-4567
    // +123.456.7890
    String PHONE_PATTERN = "^\\+?(\\d{1,3})(\\s|\\.|-)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9])|([1-9]\\d{1,2}))(\\s|\\.|-)?\\d{3}(\\s|\\.|-)?\\d{3,4}$";
}
