import React from 'react';

import { Navbar, Container, Nav, Button, ListGroup } from 'react-bootstrap';
import './main.css';

const Main = () => {
  return (
    <>
      <header>
        <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark">
          <Container>
            <Navbar.Brand href="/">Properstock</Navbar.Brand>
            <Navbar.Toggle aria-controls="responsive-navbar-nav" />
            <Navbar.Collapse id="responsive-navbar-nav">
              <Nav className="me-auto"></Nav>
              <Nav>
                <Nav.Link href="#deets">Login</Nav.Link>
                <Nav.Link href="#memes">Register</Nav.Link>
              </Nav>
            </Navbar.Collapse>
          </Container>
        </Navbar>
      </header>
      <div className="section1 d-flex align-items-center justify-content-center flex-column">
        <div>
          <h2 className="vision">
            우리 서비스는 어떤 데이터를 제공하고 그로 인해 당신은 어떤 가치를
            얻을 수 있다는 메시지 전달
          </h2>
        </div>
        <div className="info-button-area">
          <Button variant="outline-info">Get Started</Button>
        </div>
      </div>
      <div className="section2 d-flex align-items-center justify-content-center">
        <ListGroup variant="flush" className="type-container">
          <ListGroup.Item className="d-flex">
            <div className="type-name">Type A</div>
            <div className="type-desc">
              <p>정의</p>
              <p>정의</p>
            </div>
          </ListGroup.Item>
          <ListGroup.Item className="d-flex">
            <div className="type-name">Type A</div>
            <div className="type-desc">
              <p>정의</p>
              <p>정의</p>
            </div>
          </ListGroup.Item>
          <ListGroup.Item className="d-flex">
            <div className="type-name">Type A</div>
            <div className="type-desc">
              <p>정의</p>
              <p>정의</p>
            </div>
          </ListGroup.Item>
          <ListGroup.Item className="d-flex">
            <div className="type-name">Type A</div>
            <div className="type-desc">
              <p>정의</p>
              <p>정의</p>
            </div>
          </ListGroup.Item>
          <ListGroup.Item className="d-flex">
            <div className="type-name">Type A</div>
            <div className="type-desc">
              <p>정의</p>
              <p>정의</p>
            </div>
          </ListGroup.Item>
        </ListGroup>
      </div>
      <div className="section3"></div>
    </>
  );
};

Main.propTypes = {};

export default Main;
