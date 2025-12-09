<script lang="ts">
// https://icon-sets.iconify.design/material-symbols/
import Language from 'virtual:icons/material-symbols/language';
import Mail from 'virtual:icons/material-symbols/mail';
import Person from 'virtual:icons/material-symbols/account-circle';
import Business from 'virtual:icons/material-symbols/store';
import Phone from 'virtual:icons/material-symbols/call';
import Notification from 'virtual:icons/material-symbols/notifications';
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

// Diccionario: clave → componente
export const icons: Record<string, any> = {
  language: Language,
  mail: Mail,
  person: Person,
  business: Business,
  phone: Phone,
  calendar: Calendar,
  notification: Notification,
  add: Add,
  location: Location,
  address: Address,
  timer: Timer,
  money: Money,
  warning: Warning,
  star: Star,
  leftArrow: LeftArrow,
  rightArrow: RightArrow
};

function iconFor(name: string) {
  return icons[name] ?? icons["person"];
}

    let {name} = $props()
    const Icon = iconFor(name)
</script>

<Icon/>