pragma solidity >=0.4.22;

contract TutoringSession {
    
    /// The addresses of the tutor and tutee and assigning institution
    address public tutor;
    address public tutee;
    
    /// The addresses of the assigning institution and approving educator
    address public institution;
    address public approver;
    
    /// An ID specifier so that other information about the session could be
    /// queried within the institution's database
    uint public id;
    
    /// The grades of the pair at the time of the session
    string public tutor_grade;
    string public tutee_grade;
    
    /// Start, end, and duration of the tutoring session
    string public start;
    string public end;
    uint public duration_seconds;

    /// Address listings for problems and answers done during a session
    string problem_list;
    string answer_list;

    /// Private link to the chat log
    string private chat_log_url;
    
    /// Whether the session has been assigned/assessed/approved
    bool public assigned = false;
    bool public assessed = false;
    bool public approved = false;
    
    /// Comments from the approver
    string public comments;
    
    /// Initialize the session with the institution-specific ID, the tutor and tutee,
    /// their grades, and timing information about the session
    constructor (uint i, address t, address t2, string tg, string t2g, string s, string e, uint d, string cl, string pl, string al) public {
        institution = msg.sender;
        id = i;
        tutor = t;
        tutee = t2;
        tutor_grade = tg;
        tutee_grade = t2g;
        start = s;
        end = e;
        duration_seconds = d;
        chat_log_url = cl;
        problem_list = pl;
        answer_list = al;
    }

    /// Function for the originating institution to update the location
    /// of the relevant text file of the chat log
    function setChatLogLocation(string url) public {
        if (msg.sender != institution) return;
        chat_log_url = url;
    }

    /// Function to add a problem address
    
    /// Function for the originating institution to set the approver.
    /// This is only possible if not already assessed.
    function assign(address _approver) public {
        if (msg.sender != institution || assessed == true) return;
        approver = _approver;
        assigned = true;
    }

    /// Function for the assigned educator to assess the session
    function assess(bool _approved, string _comments) public {
        if (msg.sender != institution) return;
        assessed = true;
        approved = _approved;
        comments = _comments;
    }
}