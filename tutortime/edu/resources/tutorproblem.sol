pragma solidity >=0.4.22;

contract TutorProblem {
    
    /// The tutor creator and the educator approver and assigning institution
    address public creator;
    address public approver;
    address public institution;
    
    /// Creation date
    /// string public date_created;
    
    /// Whether the problem/solution has been assigned/assessed/approved
    bool public assigned = false;
    bool public assessed = false;
    bool public approved = false;
    
    /// Grade assigned by educator along with comments
    string public grade;
    string public comments;
    
    /// Private members controlling where the images are stored in object storage
    string private problem_url;
    string private solution_url;
    
    /// Initialize a new tutoring problem with a creator and access information
    /// for finding the problem and solution images
    constructor (address _creator, string _problem_url, string _solution_url) public {
        institution = msg.sender;
        creator = _creator;
        problem_url = _problem_url;
        solution_url = _solution_url;
    }
    
    /// Function for the originating institution to set the approver.
    /// This is only possible if not already assessed.
    function assign(address _approver) public {
        if (msg.sender != institution || assessed == true) return;
        approver = _approver;
        assigned = true;
    }
    
    /// Functions for the originating institution to update the location
    /// of the relevant images
    function setProblemLocation(string url) public {
        if (msg.sender != institution) return;
        problem_url = url;
    }
    function setSolutionLocation(string url) public {
        if (msg.sender != institution) return;
        solution_url = url;
    }
    
    /// Function for the assigned educator to assess the problem
    function assess(bool _approved, string _comments, string _grade) public {
        if (msg.sender != institution) return;
        assessed = true;
        approved = _approved;
        comments = _comments;
        grade = _grade;
    }
}