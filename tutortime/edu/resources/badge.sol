pragma solidity >=0.4.22;

contract Badge {
    
    /// Title of the badge and information about it
    string public title;
    string public info;
    
    /// Assigned student and assigning teacher information and assigning institution
    address public student;
    address public teacher;
    address public institution;

    /// Initialize the badge with everything all at once
    constructor (string _title, string _info, address _student, address _teacher) public {
        institution = msg.sender;
        title = _title;
        info = _info;
        student = _student;
        teacher = _teacher;
    }
}