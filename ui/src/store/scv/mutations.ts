import { SCVCredentials } from '@/types/scv'
import { State } from './state'

const SAVE_ALIASES = (state: State, aliases: string[]) => {
  state.aliases = aliases
}

const SAVE_CREDENTIALS = (state: State, credentials: SCVCredentials) => {
  state.credentials = credentials // editable in form
}

const MERGE_CREDENTIALS = (state: State, credentials: Record<string, string>) => {
  state.credentials = { ...state.credentials, ...credentials }
}

const SET_IS_EDITING = (state: State, bool: boolean) => {
  state.isEditing = bool
}

export default {
  SAVE_ALIASES,
  SAVE_CREDENTIALS,
  MERGE_CREDENTIALS,
  SET_IS_EDITING
}
