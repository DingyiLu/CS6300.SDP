pragma solidity >=0.4.22;

contract TutorAnswer {
    
    /// The address of the problem, the answerer, and the educator approver and assigning institution
    address public problem;
    address public answerer;
    address public approver;
    address public institution;
    
    /// Creation date
    /// string public date_created;
    
    /// Whether the answer attempt has been assigned/assessed/approved
    bool public assigned = false;
    bool public assessed = false;
    bool public approved = false;
    
    /// Correctness judged by educator along with comments
    bool public correct;
    string public comments;
    
    /// Private members controlling where the image is stored in object storage
    string private answer_url;
    
    /// Initialize a new tutee answer with a problem, answerer, and access information
    /// for finding the answer image
    constructor (address _problem, address _answerer, string _answer_url) public {
        institution = msg.sender;
        problem = _problem;
        answerer = _answerer;
        answer_url = _answer_url;
    }
    
    /// Function for the originating institution to set the approver.
    /// This is only possible if not already assessed.
    function assign(address _approver) public {
        if (msg.sender != institution || assessed == true) return;
        approver = _approver;
        assigned = true;
    }
    
    /// Function for the originating institution to update the location
    /// of the relevant image
    function setAnswerLocation(string url) public {
        if (msg.sender != institution) return;
        answer_url = url;
    }
    
    /// Function for the assigned educator to assess the answer
    function assess(bool _approved, bool _correct, string _comments) public {
        if (msg.sender != institution) return;
        assessed = true;
        approved = _approved;
        correct = _correct;
        comments = _comments;
    }
}