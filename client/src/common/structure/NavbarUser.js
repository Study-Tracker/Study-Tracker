import {useNavigate} from "react-router-dom";
import axios from "axios";
import {Dropdown} from "react-bootstrap";
import {LogOut, Settings, User} from "react-feather";
import React from "react";
import PropTypes from "prop-types";

const NavbarUser = ({isAdmin, userId, displayName}) => {

  const navigate = useNavigate();

  const handleLogout = async () => {
    await axios.post("/logout");
    navigate(0);
  }

  return (
    <Dropdown className="nav-item" align="end">

        <span className="d-inline-block d-sm-none">
          <Dropdown.Toggle as={"a"} className={"nav-link"}>
            <Settings size={18} className="align-middle"/>
          </Dropdown.Toggle>
        </span>

      <span className="d-none d-sm-inline-block">
          <Dropdown.Toggle as={"a"} className={"nav-link"}>
            <User/>
            <span className="text-dark">
              {displayName}
            </span>
          </Dropdown.Toggle>
        </span>

      <Dropdown.Menu drop={"end"}>

        {
          !!isAdmin
            ? (
              <Dropdown.Item as={"a"} href={"/admin"}>
                <Settings size={18}
                          className="align-middle me-2"/>
                Admin Dashboard
              </Dropdown.Item>
            ) : ''
        }

        <Dropdown.Item as={"a"} href={"/user/" + userId}>
          <User size={18} className="align-middle me-2"/>
          Profile
        </Dropdown.Item>

        <Dropdown.Item
          as={"a"}
          // href={logoutUrl}
          onClick={handleLogout}
        >
          <LogOut size={18} className="align-middle me-2"/>
          Sign out
        </Dropdown.Item>

      </Dropdown.Menu>
    </Dropdown>
  )
}

NavbarUser.propTypes = {
  isAdmin: PropTypes.bool.isRequired,
  userId: PropTypes.number.isRequired,
  displayName: PropTypes.string.isRequired,
}

export default NavbarUser;