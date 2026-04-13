export type DayOfWeek =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY';

export interface DoctorAvailability {
  id: number;
  doctorId: number;
  dayOfWeek: DayOfWeek | string;
  heureDebut: string;
  heureFin: string;
}
