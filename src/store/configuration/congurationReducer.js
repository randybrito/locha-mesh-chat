/* eslint-disable import/prefer-default-export */
import { ActionTypes } from '../constants';

const AplicationState = {
  uid: null,
  image: null,
  name: '',
  imageHash: null
};

export const configurationReducer = (state = AplicationState, action) => {
  switch (action.type) {
    case ActionTypes.INITIAL_STATE: {
      return {
        uid: action.payload.uid,
        name: action.payload.name,
        image: action.payload.picture,
        imageHash: action.payload.imageHash
      };
    }

    case ActionTypes.CLEAR_ALL: {
      return { ...AplicationState };
    }
    case ActionTypes.GET_PHOTO_USER: {
      return { ...state, image: action.payload, imageHash: action.imageHash };
    }
    case ActionTypes.EDIT_NAME: {
      return { ...state, name: action.payload };
    }
    default: {
      return state;
    }
  }
};
