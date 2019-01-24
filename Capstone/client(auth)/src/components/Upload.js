import React, { Component } from 'react';
import { get } from 'axios';
import { FilePond, File } from 'react-filepond';
import 'filepond/dist/filepond.min.css';

import '../styles/Upload.css';
import { Redirect } from 'react-router-dom';

class Upload extends Component {
  constructor(props) {
    super(props);
    this.state = {
      uploadIsComplete: false
    }

    this.serverUrl = 'http://localhost:4567';
  }

  handleError = () => {
    // TODO
    // Improve error handling/user feedback.
    // Ideally, this will require substantial additions to the UI.
    alert('We\'re having trouble uploading this file. Try another or come back later.');
  }

  handleUploadError = () => {
    alert('We\'re having trouble uploading this file. If this is a GEDCOM file you may want to try a smaller file.');
  }

  handleUploadComplete = error => {
    if (!error) {
      // Timer allows user to see final stages of upload.
      setTimeout(() => this.setState({
        uploadIsComplete: true
      }), 1000);
    }
  }

  render = () => {
    let result;
    if (this.state.uploadIsComplete) {
      result = (<Redirect push to={'/data'} />);
    } else {
      result = (
        <div className='Upload'>
          <div className='Upload__content-container'>
            <h1 className='Upload__header'>WELCOME!</h1>
            <div className='Upload__content-container--text'>
              <p className='Upload__content-text'> We're almost ready to explore your data.</p>
              <p className='Upload__content-text'> Just upload a GEDCOM file.</p>
            </div>
            <div className='Upload__content-container--text'>
              <a
                href='https://faq.myheritage.com/Family-Site/Online-Family-Tree/951686981/What-is-a-GEDCOM-file.htm' className='Upload__link'>
                What is a GEDCOM file?
              </a>
            </div>
            < FilePond
              server={`${this.serverUrl}/api`}
              onprocessfile={this.handleUploadComplete}
              onerror={this.handleUploadError}
            />
          </div>
        </div>
      );
    }
    return result;
  }
}

export default Upload;
