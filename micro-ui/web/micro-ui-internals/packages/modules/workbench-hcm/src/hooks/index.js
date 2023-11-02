import { logoutV1 } from "./logout";
import { UICreateConfigGenerator , getMDMSContextPath } from "./workbench";
import utils from "../utils";

const UserService = {
  logoutV1,
};

const workbench = {
  UICreateConfigGenerator,
  getMDMSContextPath
};

const contracts = {};

const Hooks = {
  attendance: {
    update: () => {},
  },
  workbench,
  contracts,
};

const Utils = {
  browser: {
    sample: () => {},
  },
  workbench: {
    ...utils,
  },
};

export const CustomisedHooks = {
  Hooks,
  UserService,
  Utils,
};
