/* eslint-disable import/prefer-default-export */
export const ActionTypes = {
  // APLICATION
  INITIAL_STATE: '@@aplication/INITIAL_STATE',
  GET_PHOTO: '@@aplication/GET_PHOTO',
  ROUTE: '@@aplication/ROUTE',
  CHANGE_TAB: '@@aplication/CHANGE_TAB',
  LOADING_ON: '@@aplication/LOADING_ON',
  LOADING_OFF: '@@aplication/LOADING_OFF',
  OPEN_MENU: '@@aplication/OPEN_MENU',
  CLOSE_MENU: '@@aplication/CLOSE_MENU',
  APP_STATUS: '@@aplication/VERYFY_STATUS',
  CLEAR_ALL: '@@aplication/CLEAR_ALL',
  URL_CONNECTION: '@@aplication/URL_CONNECTION',
  CONNECTION_ATTEMPT: '@@aplication/CONNECTION_ATTEMPT',
  MANUAL_CONNECTION: '@@aplication/MANUAL_CONNECTION',


  // CONFIGURATION
  GET_PHOTO_USER: '@@configuration/GET_PHOTO',
  EDIT_NAME: '@@configuration/EDIT_NAME',

  // CONTACTS

  ADD_CONTACTS: '@@contacts/ADD_CONTACTS',
  DELETE_CONTACT: '@contacts/DELETE_CONTACT',
  EDIT_CONTACT: '@contacts/EDIT_CONTACT',
  SAVE_PHOTO: '@contacts/SAVE_PHOTO',

  // CHATS

  RELOAD_BROADCAST_CHAT: '@@chat/RELOAD_BROADCAST_CHAT',
  IN_VIEW: '@@chat/IN_VIEW',
  SELECTED_CHAT: '@@chat/SELETED_CHAT',
  NEW_MESSAGE: '@@chat/NEW_MESSAGE',
  DELETE_MESSAGE: '@@chat/DELETE_MESSAGE',
  DELETE_ALL_MESSAGE: '@@chat/DELETE_ALL_MESSAGE',
  DELETE_SELECTED_MESSAGE: '@@chat/DELETE_SELECTED_MESSAGE',
  UNREAD_MESSAGES: '@@chat/UNREAD_MESSAGES',
  SET_STATUS_MESSAGE: '@@chat/SET_STATUS_MESSAGE',
  SEND_AGAIN: '@@chat/SEND_AGAIN',
  UPDATE_STATE: '@@chat/UPDATE_STATE'
};
