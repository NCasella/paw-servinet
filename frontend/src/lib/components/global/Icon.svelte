<script lang="ts">
// https://icon-sets.iconify.design/material-symbols/
import Language from 'virtual:icons/material-symbols/language';
import Mail from 'virtual:icons/material-symbols/mail';
import Person from 'virtual:icons/material-symbols/account-circle';
import Business from 'virtual:icons/material-symbols/store';
import Phone from 'virtual:icons/material-symbols/call';
import Notification from 'virtual:icons/material-symbols/notifications-rounded';
import NoNotification from 'virtual:icons/material-symbols/notifications-off-rounded';
import Calendar from 'virtual:icons/material-symbols/calendar-today';
import Add from 'virtual:icons/material-symbols/add-2';
import Location from 'virtual:icons/material-symbols/location-on';
import Address from 'virtual:icons/material-symbols/location-away-rounded';
import Timer from 'virtual:icons/material-symbols/timer-rounded';
import Money from 'virtual:icons/material-symbols/attach-money-rounded';
import Warning from 'virtual:icons/material-symbols/warning-rounded';
import Star from 'virtual:icons/material-symbols/star-rounded';
import LeftArrow from 'virtual:icons/material-symbols/arrow-left-alt-rounded';
import RightArrow from 'virtual:icons/material-symbols/arrow-right-alt-rounded';
import Schedule from 'virtual:icons/material-symbols/schedule';
import Cancel from 'virtual:icons/material-symbols/delete-outline';
import Deny from 'virtual:icons/material-symbols/x-circle-rounded';
import ArrowDropDown from 'virtual:icons/material-symbols/arrow-drop-down';
import Cleaning from 'virtual:icons/material-symbols/cleaning-rounded';
import Beauty from 'virtual:icons/material-symbols/diamond-rounded';
import Repair from 'virtual:icons/material-symbols/build-circle-rounded';
import Pet from 'virtual:icons/material-symbols/pets';
import Exteriors from 'virtual:icons/material-symbols/potted-plant-rounded';
import Celebrations from 'virtual:icons/material-symbols/celebration-rounded';
import Transport from 'virtual:icons/material-symbols/delivery-truck-speed-rounded';
import Consulting from 'virtual:icons/material-symbols/shield-question-rounded';
import HairSalon from 'virtual:icons/material-symbols/health-and-beauty-rounded';
import Health from 'virtual:icons/material-symbols/health-metrics-rounded';
import EventRepeat from 'virtual:icons/material-symbols/event-repeat';
import Accept from 'virtual:icons/material-symbols/done';
import History from 'virtual:icons/material-symbols/history-rounded';
import Info from 'virtual:icons/material-symbols/info';
import ResponseArrow from 'virtual:icons/material-symbols/subdirectory-arrow-right-rounded';
import Send from 'virtual:icons/material-symbols/send-rounded';
import Edit from 'virtual:icons/material-symbols/edit';

// Diccionario: clave → componente
export const icons: Record<string, any> = {
  language: Language,
  mail: Mail,
  person: Person,
  business: Business,
  phone: Phone,
  calendar: Calendar,
  notification: Notification,
  noNotification: NoNotification,
  edit: Edit,
  add: Add,
  location: Location,
  address: Address,
  timer: Timer,
  money: Money,
  warning: Warning,
  star: Star,
  leftArrow: LeftArrow,
  rightArrow: RightArrow,
  schedule: Schedule,
  deny: Deny,
  info: Info,
  eventRepeat: EventRepeat,
  delete: Cancel,
  accept: Accept,
  downArrow: ArrowDropDown,
  history: History,
  cleaning: Cleaning,
  beauty: Beauty,
  repair: Repair,
  pet: Pet,
  exteriors: Exteriors,
  celebrations: Celebrations,
  transport: Transport,
  consulting: Consulting,
  hairSalon: HairSalon,
  health: Health,
  responseArrow: ResponseArrow,
  send: Send,
};

function iconFor(name: string) {
  return icons[name] ?? icons["person"];
}

    let {name} = $props()
    const Icon = iconFor(name)
</script>

<Icon/>