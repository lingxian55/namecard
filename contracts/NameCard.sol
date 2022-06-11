pragma solidity ^0.8.0;

contract NameCard{
    //mapping namecard address => bytes
    mapping(address => bytes) private _namecards;
    mapping(address => mapping(address => bool)) private _allowances;
    event Apply(address sender , address received);
    function mintNameCard(bytes memory namecard) public {
        require(msg.sender!=address(0),"zero address");
        setNameCard(msg.sender,namecard);
    }
    function getNameCard(address sender) private view returns (bytes memory){
        return _namecards[sender];
    }
    function setNameCard(address onwer,bytes memory data) private returns (bytes memory){
        return _namecards[onwer]=data;
    }
    function applyForNameCard(address user) public  {
       require(msg.sender!=address(0),"zero address");
        emit Apply(msg.sender,user);
    }
    function acceptApply(address user) public {
       require(msg.sender!=address(0),"zero address");
        _allowances[msg.sender][user]=true;
    }
    function getUserNameCard(address sender) public view returns(bytes memory){
        require(msg.sender!=address(0),"zero address");
        require(sender==msg.sender||_allowances[sender][msg.sender],"permission denied");
        return getNameCard(sender);
    }
}